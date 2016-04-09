package com.xiaopo.flying.photoselecter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.xiaopo.flying.photoselecter.datatype.Album;
import com.xiaopo.flying.photoselecter.datatype.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机相册管理类
 * Created by Flying SnowBean on 2015/11/19.
 */
public class PhotoManager {
    private final String TAG = PhotoManager.class.getSimpleName();
    private ContentResolver mContentResolver;
    private Context mContext;
    private List<String> mBucketIds;

    public PhotoManager(Context context) {
        this.mContext = context;
        mContentResolver = context.getContentResolver();
        mBucketIds = new ArrayList<>();
    }

//    /**
//     * 获取相册名字以及相册封面
//     * 应异步调用此方法
//     *
//     * @return 一个包含相册名字和封面图片path的集合
//     */
//    public List<ArrayMap<String, String>> getAlbum() {
//        List<ArrayMap<String, String>> data = new ArrayList<>();
//        String projections[] = new String[]{
//                MediaStore.Images.Media.BUCKET_ID,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//        };
//        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null, MediaStore.Images.Media.DATE_MODIFIED);
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                ArrayMap<String, String> arrayMap = new ArrayMap<>();
//                String buckedId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
//
//                arrayMap.put(MediaStore.Images.Media.BUCKET_ID, buckedId);
//                arrayMap.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
//                arrayMap.put(MediaStore.Images.Media.DATA, getFrontCoverData(buckedId));
//
//                if (data.contains(arrayMap)) continue;
//
//                data.add(arrayMap);
//            } while (cursor.moveToNext());
//
//            cursor.close();
//        }
//        return data;
//    }

    public List<Album> getAlbum() {
        mBucketIds.clear();

        List<Album> data = new ArrayList<>();
        String projects[] = new String[]{
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projects
                , null
                , null
                , MediaStore.Images.Media.DATE_MODIFIED);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Album album = new Album();

                String buckedId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));

                if (mBucketIds.contains(buckedId)) continue;

                mBucketIds.add(buckedId);

                String buckedName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String coverPath = getFrontCoverData(buckedId);

                album.setId(buckedId);
                album.setName(buckedName);
                album.setCoverPath(coverPath);

                data.add(album);


            } while (cursor.moveToNext());

            cursor.close();
        }

        return data;

    }

    public List<Photo> getPhoto(String buckedId) {
        List<Photo> photos = new ArrayList<>();

        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media.DATA,MediaStore.Images.Media.DATE_ADDED,MediaStore.Images.Media.DATE_MODIFIED}
                , MediaStore.Images.Media.BUCKET_ID + "=?"
                , new String[]{buckedId}
                , MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Long dataAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                Long dataModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));


                Photo photo = new Photo(path,dataAdded,dataModified);

                photos.add(photo);

            } while (cursor.moveToNext());
            cursor.close();
        }

        return photos;
    }

    /**
     * 根据bucket_id获得相册封面的path
     *
     * @param bucketId bucket_id
     * @return 相册封面的path
     */
    private String getFrontCoverData(String bucketId) {
        String path = "empty";
        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, MediaStore.Images.Media.BUCKET_ID + "=?", new String[]{bucketId}, MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }


    public void check(List<ArrayMap<String, String>> data) {
        for (ArrayMap<String, String> item : data) {
            for (String key : item.keySet()) {
                Log.d(TAG, key + "->" + item.get(key));
            }
            Log.d(TAG, "----------------------------------");
        }
    }
}
