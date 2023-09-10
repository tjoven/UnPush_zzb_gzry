package com.wotool.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.unicom.baseoa.WebViewWnd;
import com.unicom.baseoa.update.HttpClientUtil;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpMultipartPostData extends AsyncTask<String, Integer, String> {

	private Context context;
	private File file;
	private String url;
	private String filepath;
	private String filename;
	private String guid;
	private String zid;
	private Map<String,String> paraMap;
	private ProgressDialog pd;
	private long totalSize;
	private boolean uploadSuccess = false;//表示上传是否成功

	public HttpMultipartPostData(Context context, File file,String url,Map<String,String> paraMap) {
		this.context = context;
		this.file = file;
		this.url = url;
		this.paraMap = paraMap;
		filename=file.getName();
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("文件上传中,请稍候...");
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String type = paraMap.get("type");
			ResponseBody responseBody = upload(url,file,type);
			String path = responseBody.string();

			Log.e("upload@@@", "responseBody="+ path);
			return path;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception： ", e.getMessage(),e);
		}

		return null;
	}

	public ResponseBody upload(String url, File file,String type) throws IOException {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		builder.hostnameVerifier(new SSLUtil.AnyHostnameVerifier())
				.sslSocketFactory(SSLUtil.getSSLSocketFactory(), new SSLUtil.AnyTrustManager());
		OkHttpClient client = builder.build();
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart(type, file.getName(),
						RequestBody.create(MediaType.parse("multipart/form-data"), file))
				.build();
		ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody, new ProgressListener() {
			@Override
			public void onProgress(long currentBytes, long totalBytes) {
				publishProgress((int) ((currentBytes / (float) totalBytes) * 100));
			}
		});

		Request request = new Request.Builder()
				.url(url)
				.post(progressRequestBody)
				.build();
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()){
			uploadSuccess = false;//上传失败
			throw new IOException("Unexpected code " + response);
		}else {
			uploadSuccess = true;//上传成功
		}
		return response.body();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("result: " + result);
		pd.dismiss();
		//回调js方法,显示图片
		if(uploadSuccess){//上传成功
			String callback="loadpic";
			if(paraMap.get("callback")!=null&&!"".equals(paraMap.get("callback"))){
				callback=paraMap.get("callback");
			}
			WebViewWnd wv = (WebViewWnd)context;
	        String imgSrc = filepath;
	        if(paraMap.get("jstx") != null){
				if(paraMap.get("callback").equals("showUploadFile")){
					wv.loadurl(wv.getWebView(),"javascript:"+callback+"('"+paraMap.get("fileName")+"')");
				}else{
					wv.loadurl(wv.getWebView(),"javascript:"+callback+"('"+paraMap.get("fileName")+"','"+paraMap.get("filePath")+"')");
				}
			}else{
	        	wv.loadurl(wv.getWebView(),"javascript:"+callback+"('"+zid+"','"+imgSrc+"','"+filename+"','"+guid+"')");
	        }
		}else{
			  Toast.makeText(context, "上传服务器失败，请检查网络.", Toast.LENGTH_SHORT).show();
		 }
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
	}
	public void hideProgressDialog(){
		pd.dismiss();
	}

}
