package com.tawlat.services;

import android.support.annotation.Nullable;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.Communicator;
import com.tawlat.models.Model;

import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mohammed on 2/29/16.
 */
public class Stub {

    public static class ModelParser {
        Model parseItem(String response) throws JSONException {
            return null;
        }

        ArrayList<Model> extractArray(String response) throws JSONException {
            return null;
        }

        ArrayList<Model> parseArray(String repsonse) throws JSONException {
            return null;
        }
    }

    public static void get(final String url, final Object listener, final @Nullable ModelParser parser, final @Nullable RequestParams params, final @Nullable String tag) {
        perform(0, url, listener, parser, params, tag);
    }

    public static void post(final String url, final Object listener, final @Nullable ModelParser parser, final @Nullable RequestParams params, final @Nullable String tag) {
        perform(2, url, listener, parser, params, tag);
    }

    public static void postJSON(final String url, final Object listener, final @Nullable ModelParser parser, final @Nullable RequestParams params, final @Nullable String tag) {
        perform(3, url, listener, parser, params, tag);
    }

    private static void perform(final int type, final String url, final Object listener, final @Nullable ModelParser parser, final @Nullable RequestParams params, final @Nullable String tag) {
        final Result result = new Result();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String responseString = new String(response).trim().replaceAll("\\p{C}", "");
                try {
                    result.setIsSucceeded(true);
                    executeListener(listener, result, (parser != null) ? parser.parseItem(responseString) : null, (parser != null) ? parser.extractArray(responseString) : null);
                } catch (JSONException e) {
                    e.printStackTrace();
                    result.setIsSucceeded(true);
                    result.getMessages().clear();
                    result.getMessages().add(responseString);
                    executeListener(listener, result, null, null);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                result.setIsSucceeded(false);
                result.getMessages().clear();
                try {
                    result.getMessages().add(TawlatApplication.getContext().getString(R.string.unknown_error_has_occurred));
                } catch (Exception e) {
                    e.printStackTrace();
                    result.getMessages().add(TawlatApplication.getContext().getString(R.string.unknown_error_has_occurred));
                }
                executeListener(listener, result, null, null);
            }

            @Override
            public void onCancel() {
                result.setIsSucceeded(false);
                result.getMessages().clear();
                result.setCode("0x02");
                result.getMessages().add(TawlatApplication.getContext().getString(R.string.request_got_cancelled));
                executeListener(listener, result, null, null);
                super.onCancel();
            }
        };


        if (type == 0)
            Communicator.getInstance().get(
                    url,
                    params,
                    handler,
                    tag,
                    null
            );
        else if (type == 2)
            Communicator.getInstance().post(
                    url,
                    params,
                    handler,
                    tag,
                    null
            );
        else
            Communicator.getInstance().postJSON(
                    url,
                    params,
                    handler,
                    tag,
                    null
            );
    }

    private static void executeListener(Object listener, Result result, @Nullable Model model, @Nullable ArrayList<Model> models) {
        if (listener instanceof ApiListeners.OnActionExecutedListener) {
            ((ApiListeners.OnActionExecutedListener) listener).onExecuted(result);
        } else if (listener instanceof ApiListeners.OnItemLoadedListener) {
            ((ApiListeners.OnItemLoadedListener) listener).onLoaded(result, model);
        } else if (listener instanceof ApiListeners.OnItemsArrayLoadedListener) {
            ((ApiListeners.OnItemsArrayLoadedListener) listener).onLoaded(result, models);
        }
    }
}
