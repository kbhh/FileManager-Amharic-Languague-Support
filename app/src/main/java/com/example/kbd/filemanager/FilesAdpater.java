package com.example.kbd.filemanager;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.MultiSelectListPreference;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SelectableHolder;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.example.kbd.filemanager.Files.FilesContent;
import com.example.kbd.filemanager.FilesFragment.OnListFragmentInteractionListener;
import com.example.kbd.filemanager.Files.FilesContent.FileItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FileItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FilesAdpater extends RecyclerView.Adapter<FilesAdpater.ViewHolder> {

    public   List<FileItem> fileItem;
    private final OnListFragmentInteractionListener mListener;
    public FragmentActivity fragment;
    private String previous;
    private MultiSelector selector = new MultiSelector();
    private ArrayList<String> sourceFiles = new ArrayList<>();
    private String TAG = "Files Adapter";

    private ModalMultiSelectorCallback pasteCallback = new ModalMultiSelectorCallback(selector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            fragment.getMenuInflater().inflate(R.menu.paste_menu, menu);
            return  true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            super.onDestroyActionMode(actionMode);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.paste_items) {
               onPaste(2);
                super.onDestroyActionMode(mode);
              return true;
            } else if (item.getItemId() == R.id.move_items) {
               onPaste(1);
               super.onDestroyActionMode(mode);
            }

            return  false;

        }
    };

    private ModalMultiSelectorCallback selectorCallback = new ModalMultiSelectorCallback(selector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            fragment.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public void setClearOnPrepare(boolean clearOnPrepare) {
            super.setClearOnPrepare(clearOnPrepare);
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            super.onDestroyActionMode(actionMode);

            selector.clearSelections();
            selector.refreshAllHolders();
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

               AlertDialog.Builder dialog = new AlertDialog.Builder(fragment);

               dialog.setTitle(R.string.deleteFile);
               dialog.setMessage(R.string.permssion);
               dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       deleteFiles(selector);
                   }
               });

               dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                   }
               });
               dialog.create();

                switch (item.getItemId()) {
                    case R.id.copy_item:
                         copy(selector);
                        return true;
                    case R.id.delete_item:
                        dialog.show();
                        return true;
                    case R.id.move_item:
                        copy(selector);
                }


            return false;
        }
    };

    public FilesAdpater(List<FileItem> items, OnListFragmentInteractionListener listener, FragmentActivity activity) {
        fileItem = items;
        mListener = listener;
        fragment = activity;

    }

    public void copy (MultiSelector selector) {

            sourceFiles.removeAll(sourceFiles);

            for (int i: selector.getSelectedPositions()) {
                sourceFiles.add(fileItem.get(i).filePath);
            }
            selector.setSelectable(false);
            selector.clearSelections();
            selector.refreshAllHolders();

          ((AppCompatActivity) fragment).startSupportActionMode(pasteCallback);
    }



    public void onPaste(int action) {

         String target = new File(fileItem.get(0).filePath).getParent();

         for(String src: sourceFiles) {
            copyPaste(src, target, action);
         }
        fileItem = new FilesContent(fragment).withPath(target);
         notifyDataSetChanged();

    }


    public void onMove(int action) {

        String target = new File(fileItem.get(0).filePath).getParent();

        for(String src: sourceFiles) {
            copyPaste(src, target, action);
        }
        fileItem = new FilesContent(fragment).withPath(target);
        notifyDataSetChanged();

    }


    public void copyPaste (String src, String target, int action) {
        String sdCard = Environment.getExternalStorageDirectory().toString();

        // the file to be moved or copied
        File sourceLocation = new File (src);

        // make sure your target location folder exists!
        File targetLocation = new File (target + "/" + sourceLocation.getName());

        // just to take note of the location sources
        Log.v(TAG, "sourceLocation: " + sourceLocation);
        Log.v(TAG, "targetLocation: " + targetLocation);

        try {

            // 1 = move the file, 2 = copy the file
            int actionChoice = action;

            if(actionChoice==1){

                if(sourceLocation.renameTo(targetLocation)){
                    sourceLocation.delete();
                }else{
                    Log.v(TAG, "Move file failed.");
                }

            }
            else{


                if(sourceLocation.exists()){

                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                    Log.v(TAG, "Copy file successful.");

                }else{
                    Log.v(TAG, "Copy file failed. Source file missing.");
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFiles (MultiSelector selector) {

        if (selector.getSelectedPositions().size() != 0) {
            System.out.println("Selected Items");
            System.out.println(selector.getSelectedPositions());
            System.out.println("Selected Items");

            for (int postion : selector.getSelectedPositions()){
                 String filePath = fileItem.get(postion).filePath;
                 File tempfile = new File(filePath);

                if (tempfile.exists()) {
                     boolean deleted = tempfile.getAbsoluteFile().delete();
                     System.out.println(tempfile.getAbsolutePath());
                     System.out.println(deleted);
                 }

            }

            for (int postion : selector.getSelectedPositions()){
                fileItem.remove(postion);
            }
            notifyDataSetChanged();
            selector.clearSelections();
            selector.refreshAllHolders();


        }


    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_files, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
         Drawable file_icon = null;
         Resources res =  holder.mView.getContext().getResources();
         final String fileType = fileItem.get(position).fileType.toLowerCase();

         System.out.println("File Size.......");
         System.out.println(fileItem.get(position).fileSize);
         switch (fileType) {
             case "mp3":
             case "m4a":
             case "aac":
             case "ogg":
             case "flac":
             case "wma":
             case "wav":
                 file_icon =  res.getDrawable(R.drawable.musicplayer1);
                 break;
             case "doc":
                 file_icon =  res.getDrawable(R.drawable.document);
                 break;
             case "mp4":
                 file_icon =  res.getDrawable(R.drawable.videoplayer);
                 break;
             case "pdf":
                 file_icon =  res.getDrawable(R.drawable.ic_pdf_box);
                 break;
             case "00":
                 file_icon =  res.getDrawable(R.drawable.folder1);
                 break;
             case "png":
                 file_icon = res.getDrawable(R.drawable.picture);
                 break;
             case "jpg":
                 file_icon = res.getDrawable(R.drawable.picture);
                 break;
             default:
                 file_icon = res.getDrawable(R.drawable.document);
                 break;
         }

        holder.icon.setImageDrawable(file_icon);
        holder.fileName.setText(fileItem.get(position).fileName);
       // holder.mContentView.setText(fileItem.get(position).fileType);

        if (selector.getSelectedPositions().size() == 0) {
            selector.setSelectable(false);
        }

        holder.mView.setOnClickListener(v -> {

            if (null != mListener && !holder.isActivated() && !selector.isSelectable()) {

                if (fileItem.get(position).fileType == "00") {

                    System.out.println("00000000000000");
                    System.out.println(fileItem.get(position).filePath);
                    this.previous = fileItem.get(position).filePath;
                    fileItem = new FilesContent(holder.mView.getContext()).withPath(fileItem.get(position).filePath);
                    notifyDataSetChanged();
                } else {

                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
                        String mimeType = "";
                        mimeType = myMime.getMimeTypeFromExtension(fileType);
                        newIntent.setDataAndType(Uri.fromFile( new File(fileItem.get(position).filePath)),mimeType);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            holder.mView.getContext().startActivity(newIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(holder.mView.getContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
                        }


                }
                notifyDataSetChanged();
                mListener.onListFragmentInteraction(holder.mItem);

            }
        });
    }

    @Override
    public int getItemCount() {
        return fileItem.size();
    }

    public void setTarget(String traget) {

    }

    public void backPressed(Context context) {

        if ((fileItem.size() != 0)) {
            if ((fileItem.get(0) != null )) {
                File temp = new File(fileItem.get(0).filePath).getParentFile().getParentFile();
                // System.out.println("Hello World");
                // System.out.println(temp);
                if (temp != null) {
                    String parent = temp.getAbsolutePath();
                    if (!parent.equalsIgnoreCase("/storage/emulated") && !parent.equals("/storage")) {
                        fileItem = new FilesContent(context).withPath(parent);
                        System.out.println(fileItem);
                        notifyDataSetChanged();
                    }

                }
            }
        } else {

            File temp = new File(this.previous).getParentFile();

            if (temp != null) {
                String parent = temp.getAbsolutePath();
                if (!parent.equalsIgnoreCase("/storage/emulated") && !parent.equals("/storage")) {
                    fileItem = new FilesContent(context).withPath(parent);
                    System.out.println(fileItem);
                    notifyDataSetChanged();
                }

            }
        }

    }

    public void newFolder(Context context, String fname) {

        LayoutInflater inflater = fragment.getLayoutInflater();
        final View inflatorView = inflater.inflate(R.layout.new_folder, null);

       AlertDialog.Builder dialog =  new AlertDialog.Builder(context);
                        dialog.setView(inflatorView);
                        dialog.setPositiveButton(fragment.getText(R.string.createFolder) ,new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                               EditText folderName =  inflatorView.findViewById(R.id.folder_name);

                               File tempfile = new File(fileItem.get(0).filePath);


                               File newfile = new File(tempfile.getParent() + "/" + folderName.getText());
                               System.out.println(newfile.getAbsolutePath());
                               System.out.println(newfile.mkdir());

                               fileItem = new FilesContent(context).withPath(tempfile.getParent());

                               notifyDataSetChanged();


                           }
                       });

                       dialog.setNegativeButton(fragment.getText(R.string.cancel), null);
                       dialog.create();
                       dialog.show();
    }

    public void sortSize(Context context) {

        fileItem = new FilesContent(context).sortBySize(fileItem);
        notifyDataSetChanged();
    }

    public void sortName(Context context) {
        fileItem = new FilesContent(context).sortByName(fileItem);
        notifyDataSetChanged();

    }

    public class ViewHolder extends SwappingHolder implements View.OnLongClickListener {
        public final View mView;
        public final ImageView icon;
        public final TextView fileName;
//        public final TextView mContentView;
        public FileItem mItem;

        public ViewHolder(View view) {
            super(view, selector);
            mView = view;
            icon = view.findViewById(R.id.file_icon);
            fileName =  view.findViewById(R.id.fileName);
            view.setLongClickable(true);

            view.setOnLongClickListener(view1 -> {

                if (!selector.isSelectable()) {
                    ((AppCompatActivity) fragment).startSupportActionMode(selectorCallback);

                    selector.setSelectable(true);
                    selector.setSelected(ViewHolder.this, true);
                    return  true;
                } else {
                    if (selector.isSelected(getLayoutPosition(), getItemId())) {

                        selector.setSelected(ViewHolder.this, false);
                        if (selector.getSelectedPositions().size() == 0) {
                            selector.setSelectable(false);
                        }

                        return true;
                    } else {
                        selector.setSelected(ViewHolder.this, true);


                        return true;
                    }

                }

            });

        }


        @Override
        public String toString() {
            return super.toString() + " '" +  "'";
        }


        @Override
        public boolean onLongClick(View view) {

            System.out.println("Long Click");
            if (!selector.isSelectable()) {
                selector.setSelectable(true);
                selector.setSelected(ViewHolder.this, true);
                return  true;

            }

            return false;
        }

    }
}
