package com.tawlat.core;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tawlat.R;
import com.tawlat.TawlatApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Communicator {

    class Request {
        final static int GET = 0;
        final static int POST = 1;
        final static int POST_JSON = 1;

        public Request(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, String tag, Header[] headers, int type) {
            this.url = url;
            this.requestParams = requestParams;
            this.handler = handler;
            this.tag = tag;
            this.headers = headers;
            this.type = type;
        }

        String url;
        RequestParams requestParams;
        AsyncHttpResponseHandler handler;
        String tag;
        Header[] headers;
        int type;
    }

    public final static String BASE_URL = "http://tawlat.com/";
    public final static String API_URL = BASE_URL + "TawlatServices.svc/";
    private final static int TIME_OUT = 20 * 1000;
    private final static int MAX_NUMBER_OF_PARALLEL_REQUESTS = 10;

    private final AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    private final ArrayList<Request> mPendingRequests = new ArrayList<>();
    private int mNumberOfParallelRequests = 0;
    private final Context mContext;

    private static Communicator mInstance;

    public static Communicator getInstance() {
        if (mInstance == null)
            mInstance = new Communicator(TawlatApplication.getContext());
        return mInstance;
    }

    private Communicator(Context context) {
        mContext = context;
    }

    public void get(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, String tag, Header[] headers) {
        if (!checkIfCanRun(url, requestParams, handler, tag, headers, Request.GET)) return;
        handler = wrapHandler(handler, tag);
        url = (url.startsWith("http")) ? url : getAbsoluteUrl(url);
        Log.d("Communicator", "The url is: " + url);
        Log.d("Communicator", "The params are: " + requestParams.toString());

        mAsyncHttpClient.setTimeout(TIME_OUT);
        mAsyncHttpClient.get(mContext, url, headers, requestParams, handler).setTag(tag);
    }

    public void get(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, Header[] headers) {
        get(url, requestParams, handler, System.nanoTime() + "", headers);
    }

    public void get(String url, RequestParams requestParams, AsyncHttpResponseHandler handler) {
        get(url, requestParams, handler, System.nanoTime() + "", null);
    }

    public void post(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, String tag, Header[] headers) {
        if (!checkIfCanRun(url, requestParams, handler, tag, headers, Request.POST)) return;
        handler = wrapHandler(handler, tag);
        url = (url.startsWith("http")) ? url : getAbsoluteUrl(url);
        Log.d("Communicator", "The url is: " + url);
        Log.d("Communicator", "The params are: " + requestParams.toString());

        mAsyncHttpClient.setTimeout(TIME_OUT);
        mAsyncHttpClient.post(mContext, url, headers, requestParams, null, handler).setTag(tag);
    }

    public void post(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, Header[] headers) {
        post(url, requestParams, handler, System.nanoTime() + "", headers);
    }

    public void post(String url, RequestParams requestParams, AsyncHttpResponseHandler handler) {
        post(url, requestParams, handler, System.nanoTime() + "", null);
    }


    public void postJSON(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, String tag, Header[] headers) {
        try {
            if (!checkIfCanRun(url, requestParams, handler, tag, headers, Request.POST_JSON))
                return;
            Log.d("Communicator", "The params are: " + requestParams.toString());
            JSONObject jsonObject = new JSONObject();
            String[] keysValues = requestParams.toString().split("&");
            for (String keyValue : keysValues) {
                String[] kv = keyValue.split("=");
                String key = kv[0];
                String value = kv.length == 2 ? kv[1] : "";
                jsonObject.put(key, value);
            }

            ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
            entity.setContentEncoding("UTF-8");


            handler = wrapHandler(handler, tag);
            url = (url.startsWith("http")) ? url : getAbsoluteUrl(url);
            Log.d("Communicator", "The url is: " + url);
            Log.d("Communicator", "The params are: " + jsonObject.toString());

            mAsyncHttpClient.setTimeout(TIME_OUT);
            mAsyncHttpClient.post(TawlatApplication.getContext(), url, entity, "application/x-www-form-urlencoded", handler);

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void postJSON(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, Header[] headers) {
        postJSON(url, requestParams, handler, System.nanoTime() + "", headers);
    }

    public void postJSON(String url, RequestParams requestParams, AsyncHttpResponseHandler handler) {
        postJSON(url, requestParams, handler, System.nanoTime() + "", null);
    }

    public void cancelAll() {
        mPendingRequests.clear();
        mAsyncHttpClient.cancelRequests(mContext, true);
    }

    public void cancelByTag(String tag) {
        Iterator<Request> iterator = mPendingRequests.iterator();
        while (iterator.hasNext()) {
            Request request = iterator.next();
            if (request.tag.equals(tag))
                iterator.remove();
        }
        mAsyncHttpClient.cancelRequestsByTAG(tag, true);
    }

    public synchronized boolean checkIfCanRun(String url, RequestParams requestParams, AsyncHttpResponseHandler handler, String tag, Header[] headers, int type) {
        if (mNumberOfParallelRequests >= MAX_NUMBER_OF_PARALLEL_REQUESTS) {
            mPendingRequests.add(new Request(url, requestParams, handler, tag, headers, type));
            return false;
        }
        mNumberOfParallelRequests++;
        return true;
    }

    private AsyncHttpResponseHandler wrapHandler(final AsyncHttpResponseHandler handler, final String tag) {
        AsyncHttpResponseHandler wrapper = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                handler.onSuccess(statusCode, headers, response);
                Log.d("Communicator", "Request '" + tag + "': " + new String(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseString, Throwable throwable) {
                String result = responseString == null ? TawlatApplication.getContext().getString(R.string.unknown_error_has_occurred) : new String(responseString);

                Log.d("Communicator", "Request '" + tag + "': " + result);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", TawlatApplication.getContext().getString(R.string.unknown_error_has_occurred));
                    handler.onFailure(statusCode, headers, result.getBytes(), throwable);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFinish() {
                super.onFinish();
                mNumberOfParallelRequests--;
                engageNewRequest();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                handler.onCancel();
                mNumberOfParallelRequests--;
                engageNewRequest();
            }
        };


        wrapper.setTag(tag);
        return wrapper;
    }

    private String getAbsoluteUrl(String url) {
        return API_URL + url;
    }

    private synchronized void engageNewRequest() {
        while (mPendingRequests.size() >= 1 && mNumberOfParallelRequests < MAX_NUMBER_OF_PARALLEL_REQUESTS) {
            Request r = mPendingRequests.remove(0);
            if (r.type == Request.GET) {
                get(r.url, r.requestParams, r.handler, r.tag, r.headers);
            } else if (r.type == Request.POST) {
                post(r.url, r.requestParams, r.handler, r.tag, r.headers);
            } else {
                postJSON(r.url, r.requestParams, r.handler, r.tag, r.headers);
            }
        }
    }
}
