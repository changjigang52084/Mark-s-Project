/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hch.filedownloader.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hch.filedownloader.model.FileDownloadModel;
import com.hch.filedownloader.model.FileDownloadStatus;
import com.hch.filedownloader.util.FileDownloadHelper;
import com.hch.filedownloader.util.FileDownloadLog;

/**
 * Created by Jacksgong on 9/24/15.
 */
class FileDownloadDBHelper implements IFileDownloadDBHelper {

	private final SQLiteDatabase db;

	public final static String TABLE_NAME = "filedownloader"; //下载进度

	private final Map<Integer, FileDownloadModel> downloaderModelMap = new HashMap<>();

	public FileDownloadDBHelper(Context context) {
		Context sever = FileDownloadHelper.getAppContext() == null ? context.getApplicationContext()
				: FileDownloadHelper.getAppContext();

		FileDownloadDBOpenHelper openHelper = new FileDownloadDBOpenHelper(sever);

		db = openHelper.getWritableDatabase();

		refreshDataFromDB();
	}

	@Override
	public Set<FileDownloadModel> getAllUnComplete() {
		return null;
	}

	@Override
	public Set<FileDownloadModel> getAllCompleted() {
		return null;
	}

	@Override
	public void refreshDataFromDB() {
		// TODO 优化，分段加载，数据多了以后
		// TODO 自动清理一个月前的数据
		long start = System.currentTimeMillis();
		Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

		List<Integer> dirtyList = new ArrayList<>();
		try {
			while (c.moveToNext()) {
				FileDownloadModel model = new FileDownloadModel();
				model.setId(c.getInt(c.getColumnIndex(FileDownloadModel.ID)));
				model.setUrl(c.getString(c
						.getColumnIndex(FileDownloadModel.URL)));
				model.setUrlKey(c.getString(c.getColumnIndex(FileDownloadModel.URL_KEY)));
				model.setPath(c.getString(c
						.getColumnIndex(FileDownloadModel.PATH)));
				// model.setCallbackProgressTimes(c.getInt(c.getColumnIndex(FileDownloadModel.CALLBACK_PROGRESS_TIMES)));
				model.setStatus((byte) c.getShort(c
						.getColumnIndex(FileDownloadModel.STATUS)));
				model.setSoFar(c.getInt(c
						.getColumnIndex(FileDownloadModel.SOFAR)));
				model.setTotal(c.getInt(c
						.getColumnIndex(FileDownloadModel.TOTAL)));
				model.setErrMsg(c.getString(c
						.getColumnIndex(FileDownloadModel.ERR_MSG)));
				model.setETag(c.getString(c
						.getColumnIndex(FileDownloadModel.ETAG)));
				if (model.getStatus() == FileDownloadStatus.progress
						|| model.getStatus() == FileDownloadStatus.connected
						|| model.getStatus() == FileDownloadStatus.error) {
					// 保证断点续传可以覆盖到
					model.setStatus(FileDownloadStatus.paused);
				}

				// consider check in new thread, but SQLite lock | file lock
				// aways effect, so sync
				if (model.getStatus() == FileDownloadStatus.pending) {
					// 脏数据 在数据库中是pending或是progress，说明是之前
					dirtyList.add(model.getId());
				} else if (!FileDownloadMgr.checkReuse(model.getId(), model)
						&& !FileDownloadMgr.checkBreakpointAvailable(
								model.getId(), model)) {
					// can't use to reuse old file & can't use to resume form
					// break point
					// = dirty
					dirtyList.add(model.getId());
				}
				downloaderModelMap.put(model.getId(), model);
			}
		} finally {
			c.close();

			for (Integer integer : dirtyList) {
				downloaderModelMap.remove(integer);
			}

			// db
			if (dirtyList.size() > 0) {
				String args = TextUtils.join(", ", dirtyList);
				if (FileDownloadLog.NEED_LOG) {
					FileDownloadLog.d(this, "delete %s", args);
				}
				db.execSQL(String.format("DELETE FROM %s WHERE %s IN (%s);",
						TABLE_NAME, FileDownloadModel.ID, args));
			}

			// 566 data consumes about 140ms
			if (FileDownloadLog.NEED_LOG) {
				FileDownloadLog.d(this,
						"refresh data %d , will delete: %d consume %d",
						downloaderModelMap.size(), dirtyList.size(),
						System.currentTimeMillis() - start);
			}
		}

	}

	@Override
	public FileDownloadModel find(final int id) {
		return downloaderModelMap.get(id);
	}

	@Override
	public void insert(FileDownloadModel downloadModel) {
		downloaderModelMap.put(downloadModel.getId(), downloadModel);

		// db
		db.insert(TABLE_NAME, null, downloadModel.toContentValues());
	}

	@Override
	public void update(FileDownloadModel downloadModel) {
		if (downloadModel == null) {
			FileDownloadLog.w(this, "update but model == null!");
			return;
		}

		if (find(downloadModel.getId()) != null) {
			// 替换
			downloaderModelMap.remove(downloadModel.getId());
			downloaderModelMap.put(downloadModel.getId(), downloadModel);

			// db
			ContentValues cv = downloadModel.toContentValues();
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(downloadModel.getId()) });
		} else {
			insert(downloadModel);
		}
	}

	@Override
	public void remove(int id) {
		downloaderModelMap.remove(id);

		// db
		db.delete(TABLE_NAME, FileDownloadModel.ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	private long lastRefreshUpdate = 0;

	@Override
	public void update(int id, byte status, long soFar, long total) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setStatus(status);
			downloadModel.setSoFar(soFar);
			downloadModel.setTotal(total);

			boolean needRefresh2DB = false;
			final int MIN_REFRESH_DURATION_2_DB = 10;
			if (System.currentTimeMillis() - lastRefreshUpdate > MIN_REFRESH_DURATION_2_DB) {
				needRefresh2DB = true;
				lastRefreshUpdate = System.currentTimeMillis();
			}

			if (!needRefresh2DB) {
				return;
			}

			// db
			ContentValues cv = new ContentValues();
			cv.put(FileDownloadModel.STATUS, status);
			cv.put(FileDownloadModel.SOFAR, soFar);
			cv.put(FileDownloadModel.TOTAL, total);
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(id) });
		}

	}

	//更新下载地址密钥
	@Override
	public void updateUrlKey(int id, String urlKey) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setUrlKey(urlKey);
			// db
			ContentValues cv = new ContentValues();
			cv.put(FileDownloadModel.URL_KEY, urlKey);
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(id) });
		}
	}

	@Override
	public void updateHeader(int id, String etag) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setETag(etag);

			// db
			ContentValues cv = new ContentValues();
			cv.put(FileDownloadModel.ETAG, etag);
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(id) });
		}
	}

	@Override
	public void updateError(int id, String errMsg) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setStatus(FileDownloadStatus.error);
			downloadModel.setErrMsg(errMsg);

			// db
			ContentValues cv = new ContentValues();
			cv.put(FileDownloadModel.ERR_MSG, errMsg);
			cv.put(FileDownloadModel.STATUS, FileDownloadStatus.error);
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(id) });
		}
	}

	@Override
	public void updateRetry(int id, String errMsg, int retryingTimes) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setStatus(FileDownloadStatus.retry);
			downloadModel.setErrMsg(errMsg);

			// db
			ContentValues cv = new ContentValues();
			cv.put(FileDownloadModel.ERR_MSG, errMsg);
			cv.put(FileDownloadModel.STATUS, FileDownloadStatus.retry);
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(id) });
		}
	}

	@Override
	public void updateComplete(int id, final long total) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setStatus(FileDownloadStatus.completed);
			downloadModel.setSoFar(total);
			downloadModel.setTotal(total);
		}

		// db
		ContentValues cv = new ContentValues();
		cv.put(FileDownloadModel.STATUS, FileDownloadStatus.completed);
		cv.put(FileDownloadModel.TOTAL, total);
		cv.put(FileDownloadModel.SOFAR, total);
		db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
				new String[] { String.valueOf(id) });
	}

	@Override
	public void updatePause(int id) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setStatus(FileDownloadStatus.paused);

			// db
			ContentValues cv = new ContentValues();
			cv.put(FileDownloadModel.STATUS, FileDownloadStatus.paused);
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(id) });
		}
	}

	@Override
	public void updatePending(int id) {
		final FileDownloadModel downloadModel = find(id);
		if (downloadModel != null) {
			downloadModel.setStatus(FileDownloadStatus.pending);

			// db
			ContentValues cv = new ContentValues();
			cv.put(FileDownloadModel.STATUS, FileDownloadStatus.pending);
			db.update(TABLE_NAME, cv, FileDownloadModel.ID + " = ? ",
					new String[] { String.valueOf(id) });
		}
	}
}
