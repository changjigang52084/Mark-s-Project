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

package com.hch.filedownloader.event;

import android.os.Handler;
import android.os.Looper;

import com.hch.filedownloader.util.FileDownloadLog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by duanchunlin on 15/6/23.
 */
public class DownloadEventPoolImpl implements IDownloadEventPool {

    private final ExecutorService threadPool = new ThreadPoolExecutor(3, 30,
            10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final HashMap<String, LinkedList<IDownloadListener>> listenersMap = new HashMap<>();

    private final Handler handler;

    public DownloadEventPoolImpl() {
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean addListener(final String eventId, final IDownloadListener listener) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "setListener %s", eventId);
        }
//        Assert.assertNotNull("EventPoolImpl.add", listener);

        LinkedList<IDownloadListener> container = listenersMap.get(eventId);

        if (container == null) {
            synchronized (eventId) {
                container = listenersMap.get(eventId);
                if (container == null) {
                    listenersMap.put(eventId, container = new LinkedList<>());
                }
            }
        }


        synchronized (eventId) {
            return container.add(listener);
        }
    }

    @Override
    public boolean removeListener(final String eventId, final IDownloadListener listener) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "removeListener %s", eventId);
        }
//        Assert.assertNotNull("EventPoolImpl.remove", listener);

        LinkedList<IDownloadListener> container = listenersMap.get(eventId);
        if (container == null) {
            synchronized (eventId) {
                container = listenersMap.get(eventId);
            }
        }

        if (container == null || listener == null) {
            return false;
        }

        synchronized (eventId) {
            boolean succeed = container.remove(listener);
            if (container.size() <= 0) {//by 2016.3.26
                listenersMap.remove(eventId);           
            }
            return succeed;
        }
    }

    public boolean post2UI(final Runnable runnable) {
        if (handler == null) {
            return false;
        }
        return handler.post(runnable);
    }

    @Override
    public boolean publish(final IDownloadEvent event) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "publish %s", event.getId());
        }
//        Assert.assertNotNull("EventPoolImpl.publish", event);
        String eventId = event.getId();
        LinkedList<IDownloadListener> listeners = listenersMap.get(eventId);
        if (listeners == null) {
            synchronized (eventId) {
                listeners = listenersMap.get(eventId);
                if (listeners == null) {
                    if (FileDownloadLog.NEED_LOG) {
                        FileDownloadLog.d(this, "No listener for this event %s", eventId);
                    }
                    return false;
                }
            }
        }

        trigger(listeners, event);
        return true;
    }

    @Override
    public void asyncPublish(final IDownloadEvent event, final Looper looper) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "asyncPublish %s", event.getId());
        }
//        Assert.assertNotNull("EventPoolImpl.asyncPublish event", event);
//        Assert.assertNotNull("EventPoolImpl.asyncPublish looper", looper);
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                DownloadEventPoolImpl.this.publish(event);
            }
        });
    }


    @Override
    public void asyncPublishInNewThread(final IDownloadEvent event) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "asyncPublishInNewThread %s", event.getId());
        }
//        Assert.assertNotNull("EventPoolImpl.asyncPublish event", event);

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                DownloadEventPoolImpl.this.publish(event);
            }
        });
    }

    @Override
    public void asyncPublishInMain(final IDownloadEvent event) {
        post2UI(new Runnable() {
            @Override
            public void run() {
                DownloadEventPoolImpl.this.publish(event);
            }
        });
    }

    private void trigger(final LinkedList<IDownloadListener> listeners, final IDownloadEvent event) {
        // do not handle Order.
//        try {
//            if (event.getOrder()) {
//                Collections.sort(listeners, new Comparator<IDownloadListener>() {
//                    @Override
//                    public int compare(IDownloadListener lhs, IDownloadListener rhs) {
//                        return rhs.getPriority() - lhs.getPriority();
//                    }
//                });
//            }
//
//        } catch (Exception e) {
//            FileDownloadLog.e(this, e, "trigger error, %s", event != null ? event.getId() : null);
//        }

        final Object[] lists = listeners.toArray();
        for (Object o : lists) {
            if (((IDownloadListener) o).callback(event)) {
                break;
            }
        }

        if (event.callback != null) {
            event.callback.run();
        }
    }

    @Override
    public boolean hasListener(final IDownloadEvent event) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "hasListener %s", event.getId());
        }
//        Assert.assertNotNull("EventPoolImpl.hasListener", event);
        String eventId = event.getId();
        LinkedList<IDownloadListener> listeners = listenersMap.get(eventId);
        return listeners != null && listeners.size() > 0;
    }
}
