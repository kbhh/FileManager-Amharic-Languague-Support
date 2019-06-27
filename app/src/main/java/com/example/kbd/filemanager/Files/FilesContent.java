package com.example.kbd.filemanager.Files;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class FilesContent {

    /**
     * An array of sample (dummy) items.
     */

    public Context context;


    /**
     * A map of sample (dummy) items, by ID.
     */
    public FilesContent(Context con) {
        context = con;

    }


   public List<FileItem> getInternal() {

       List<FileItem> internalFiles = new ArrayList<>();
       File[] external = Environment.getExternalStorageDirectory().listFiles();

       Arrays.sort(external, new Comparator<File>() {
           @Override
           public int compare(File file, File t1) {
                String ext1 = "00";
                String ext2 = "00";
               if (!file.isDirectory()) {
                    ext1 =  file.getAbsolutePath().toString().substring(file.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
               }
               if (!t1.isDirectory()) {
                   ext2 =  t1.getAbsolutePath().toString().substring(t1.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
               }
               return ext1.compareTo(ext2);
           }
       });

       for (int i=0; i < external.length; i++) {
           File tmp = external[i];
           if (!tmp.isHidden()) {
               String ext = "00";
               if (!tmp.isDirectory()) {

                   ext =  tmp.getAbsolutePath().toString().substring(tmp.getAbsolutePath().lastIndexOf(".") + 1);
               }
               internalFiles.add(createFileItem(tmp.getAbsolutePath(), tmp.getName(), ext, (float) tmp.length()));
           }
       }

       return internalFiles;

   }


    public List<FileItem> getSd() {

        List<FileItem> sdFiles = new ArrayList<>();
        File[] external = new File("/storage/extSdCard").listFiles();

        Arrays.sort(external, (file, t1) -> {
            String ext1 = "00";
            String ext2 = "00";
            if (!file.isDirectory()) {
                ext1 =  file.getAbsolutePath().toString().substring(file.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
            }
            if (!t1.isDirectory()) {
                ext2 =  t1.getAbsolutePath().toString().substring(t1.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
            }

            return ext1.compareTo(ext2);
        });

        for (int i=0; i < external.length; i++) {
            File tmp = external[i];
            if (!tmp.isHidden()) {
                String ext = "00";
                if (!tmp.isDirectory()) {

                    ext =  tmp.getAbsolutePath().toString().substring(tmp.getAbsolutePath().lastIndexOf(".") + 1);
                }
                sdFiles.add(createFileItem(tmp.getAbsolutePath(), tmp.getName(), ext, (float) tmp.length()));
            }
        }

        return sdFiles;

    }

   public List<FileItem> getImages() {
        List<FileItem> images = new ArrayList<>();
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, // Which columns to return
                    null,       // Return all rows
                    null,
                    null);


            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {

            } else {

                int thumbID = 0;
                while (cursor.moveToNext()) {

                    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    String path = cursor.getString(file_ColumnIndex);
                    File tmp = new File(path);
                    String ext =  tmp.getAbsolutePath().toString().substring(tmp.getAbsolutePath().lastIndexOf(".") + 1);
                    images.add(createFileItem(tmp.getAbsolutePath(), tmp.getName(), ext, (float) tmp.length()));

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
   }


   public List<FileItem> sortBySize(List<FileItem> files) {
        FileItem[] fileItems = files.toArray(new FileItem[0]);

        Arrays.sort(fileItems, (fileItem, t1) -> fileItem.fileSize.compareTo(t1.fileSize));

        List<FileItem> list = Arrays.asList(fileItems);
        return  list;
   }


   public List<FileItem> sortByName(List<FileItem> files) {
       FileItem[] fileItems = files.toArray(new FileItem[0]);

       Arrays.sort(fileItems, (fileItem, t1) -> fileItem.fileName.compareTo(t1.fileName));

       List<FileItem> list = Arrays.asList(fileItems);
       return  list;
   }

   //Return list of videos
   public List<FileItem> getVideos() {
       List<FileItem> videos = new ArrayList<>();
       try {
           String[] projection = {MediaStore.Video.Media.DATA};
           Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                   projection, // Which columns to return
                   null,       // Return all rows
                   null,
                   null);


           int size = cursor.getCount();

           /*******  If size is 0, there are no images on the SD Card. *****/

           if (size == 0) {

           } else {

               int thumbID = 0;
               while (cursor.moveToNext()) {

                   int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

                   /**************** Captured image details ************/

                   /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                   String path = cursor.getString(file_ColumnIndex);
                   File tmp = new File(path);
                   String ext =  tmp.getAbsolutePath().toString().substring(tmp.getAbsolutePath().lastIndexOf(".") + 1);
                   videos.add(createFileItem(tmp.getAbsolutePath(), tmp.getName(), ext, (float) tmp.length()));

               }
           }


       } catch (Exception e) {
           e.printStackTrace();
       }

       return videos;
   }


    public List<FileItem> getFiles() {
        List<FileItem> files = new ArrayList<>();
        try {

            ContentResolver cr = context.getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");

            String[] projection = null;


            String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
            String[] selectionArgsPdf = new String[]{ mimeType };

            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
            String[] selectionArgs = null; // there is no ? in selection so null here

            String sortOrder = null; // unordered
            Cursor allNonMediaFiles = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);



            int size = allNonMediaFiles.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {

            } else {

                int thumbID = 0;
                while (allNonMediaFiles.moveToNext()) {

                    int file_ColumnIndex = allNonMediaFiles.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    String path = allNonMediaFiles.getString(file_ColumnIndex);
                    File tmp = new File(path);
                    String ext =  tmp.getAbsolutePath().toString().substring(tmp.getAbsolutePath().lastIndexOf(".") + 1);

                    System.out.print(tmp.getTotalSpace());
                    files.add(createFileItem(tmp.getAbsolutePath(), tmp.getName(), ext, (float) tmp.length()));

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
//
        return files;
    }


    public List<FileItem> getAudios() {
        List<FileItem> audios = new ArrayList<>();
        try {
            String[] projection = {MediaStore.Audio.Media.DATA};
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, // Which columns to return
                    null,       // Return all rows
                    null,
                    null);


            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {

            } else {

                int thumbID = 0;
                while (cursor.moveToNext()) {

                    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    String path = cursor.getString(file_ColumnIndex);
                    File tmp = new File(path);
                    String ext =  tmp.getAbsolutePath().toString().substring(tmp.getAbsolutePath().lastIndexOf(".") + 1);
                    audios.add(createFileItem(tmp.getAbsolutePath(), tmp.getName(), ext, (float) tmp.length()));

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
//        Arrays.sort(images, new Comparator<FileItem>() {
//
//        });
        return audios;
    }

    private static FileItem createFileItem(String path, String fileName, String type, Float fileSize) {
        return new FileItem(path, fileName, type, fileSize);
    }

    public  List<FileItem> withPath(String path) {
        List<FileItem> sdFiles = new ArrayList<>();
        File[] external = new File(path).listFiles();

        Arrays.sort(external, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                String ext1 = "00";
                String ext2 = "00";
                if (!file.isDirectory()) {
                    ext1 =  file.getAbsolutePath().toString().substring(file.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
                }
                if (!t1.isDirectory()) {
                    ext2 =  t1.getAbsolutePath().toString().substring(t1.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
                }

                return ext1.compareTo(ext2);
            }
        });

        for (int i=0; i < external.length; i++) {
            File tmp = external[i];
            if (!tmp.isHidden()) {
                String ext = "00";
                if (!tmp.isDirectory()) {

                    ext =  tmp.getAbsolutePath().toString().substring(tmp.getAbsolutePath().lastIndexOf(".") + 1);
                }
                sdFiles.add(createFileItem(tmp.getAbsolutePath(), tmp.getName(), ext, (float) tmp.getTotalSpace()));
            }
        }

        return sdFiles;
    }

    public List<FileItem> search(String query) {

        List<FileItem> result = new ArrayList<>();
        for (FileItem item : getSd()) {
            if (item.filePath.toLowerCase().contains(query.toLowerCase()) || item.fileName.toLowerCase().contains(query.toLowerCase())) {
                result.add(item);
            }
        }

        for (FileItem item : getInternal()) {
            if (item.filePath.toLowerCase().contains(query.toLowerCase()) || item.fileName.toLowerCase().contains(query.toLowerCase())) {
                result.add(item);
            }
        }
        for (FileItem item : getAudios()) {
            if (item.filePath.toLowerCase().contains(query.toLowerCase()) || item.fileName.toLowerCase().contains(query.toLowerCase())) {
                result.add(item);
            }
        }
        for (FileItem item : getVideos()) {
            if (item.filePath.toLowerCase().contains(query.toLowerCase()) || item.fileName.toLowerCase().contains(query.toLowerCase())) {
                result.add(item);
            }
        }
        for (FileItem item : getImages()) {
            if (item.filePath.toLowerCase().contains(query.toLowerCase()) || item.fileName.toLowerCase().contains(query.toLowerCase())) {
                result.add(item);
            }
        }

        return result;
    }



    /**
     * A dummy item representing a piece of content.
     */
    public static class FileItem {
        public  String filePath;
        public  String fileName;
        public  String fileType;
        public  Float fileSize;


        public FileItem(String filePath, String fileName, String fileType, Float fileSize) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }
}
