package top.baixiaoshengzjj.mygraduationapp.http.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.exception.CancelException;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.HttpException;
import com.hjq.http.exception.NetworkException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.exception.ResultException;
import com.hjq.http.exception.ServerException;
import com.hjq.http.exception.TimeoutException;
import com.hjq.http.exception.TokenException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.helper.ActivityStackManager;
import top.baixiaoshengzjj.mygraduationapp.http.json.BooleanTypeAdapter;
import top.baixiaoshengzjj.mygraduationapp.http.json.DoubleTypeAdapter;
import top.baixiaoshengzjj.mygraduationapp.http.json.FloatTypeAdapter;
import top.baixiaoshengzjj.mygraduationapp.http.json.IntegerTypeAdapter;
import top.baixiaoshengzjj.mygraduationapp.http.json.ListTypeAdapter;
import top.baixiaoshengzjj.mygraduationapp.http.json.LongTypeAdapter;
import top.baixiaoshengzjj.mygraduationapp.http.json.StringTypeAdapter;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.SignInActivity;

/**
 *    author : Android ?????????
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : ???????????????
 */
public final class RequestHandler implements IRequestHandler {

    private final Application mApplication;

    private Gson mGson;

    public RequestHandler(Application application) {
        mApplication = application;
    }

    @Override
    public Object requestSucceed(LifecycleOwner lifecycle, IRequestApi api, Response response, Type type) throws Exception {

        if (Response.class.equals(type)) {
            return response;
        }

        if (!response.isSuccessful()) {
            // ??????????????????
            throw new ResponseException(mApplication.getString(R.string.http_response_error) + "???responseCode???" + response.code() + "???message???" + response.message(), response);
        }

        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }

        if (Bitmap.class.equals(type)) {
            // ?????????????????? Bitmap ??????
            return BitmapFactory.decodeStream(body.byteStream());
        }

        String text;
        try {
            text = body.string();
        } catch (IOException e) {
            // ????????????????????????
            throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
        }

        // ???????????? Json
        EasyLog.json(text);

        final Object result;
        if (String.class.equals(type)) {
            // ?????????????????? String ??????
            result = text;
        } else if (JSONObject.class.equals(type)) {
            try {
                // ?????????????????? JSONObject ??????
                result = new JSONObject(text);
            } catch (JSONException e) {
                throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
            }
        } else if (JSONArray.class.equals(type)) {
            try {
                // ?????????????????? JSONArray ??????
                result = new JSONArray(text);
            }catch (JSONException e) {
                throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
            }
        } else {

            try {
                if (mGson == null) {
                    // Json ????????????
                    mGson = new GsonBuilder()
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, new StringTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(boolean.class, Boolean.class, new BooleanTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(int.class, Integer.class, new IntegerTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(long.class, Long.class, new LongTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(float.class, Float.class, new FloatTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(double.class, Double.class, new DoubleTypeAdapter()))
                            .registerTypeHierarchyAdapter(List.class, new ListTypeAdapter())
                            .create();
                }
                result = mGson.fromJson(text, type);
            } catch (JsonSyntaxException e) {
                // ????????????????????????
                throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
            }

            if (result instanceof HttpData) {
                HttpData model = (HttpData) result;
                if (model.getCode() == 0) {
                    // ??????????????????
                    return result;
                } else if (model.getCode() == 1001) {
                    // ???????????????????????????????????????
                    throw new TokenException(mApplication.getString(R.string.http_account_error));
                } else {
                    // ??????????????????
                    throw new ResultException(model.getMessage(), model);
                }
            }
        }
        return result;
    }

    @Override
    public Exception requestFail(LifecycleOwner lifecycle, IRequestApi api, Exception e) {
        // ???????????????????????????????????????
        if (e instanceof HttpException) {
            if (e instanceof TokenException) {
                // ???????????????????????????????????????
                Application application = ActivityStackManager.getInstance().getApplication();
                Intent intent = new Intent(application, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.startActivity(intent);
                // ????????????????????????????????????
                ActivityStackManager.getInstance().finishAllActivities(SignInActivity.class);
            }
        } else {
            if (e instanceof SocketTimeoutException) {
                e = new TimeoutException(mApplication.getString(R.string.http_server_out_time), e);
            } else if (e instanceof UnknownHostException) {
                NetworkInfo info = ((ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                // ????????????????????????
                if (info != null && info.isConnected()) {
                    // ?????????????????????????????????
                    e = new ServerException(mApplication.getString(R.string.http_server_error), e);
                } else {
                    // ??????????????????????????????
                    e = new NetworkException(mApplication.getString(R.string.http_network_error), e);
                }
            } else if (e instanceof IOException) {
                //e = new CancelException(context.getString(R.string.http_request_cancel), e);
                e = new CancelException("", e);
            } else {
                e = new HttpException(e.getMessage(), e);
            }
        }
        return e;
    }
}