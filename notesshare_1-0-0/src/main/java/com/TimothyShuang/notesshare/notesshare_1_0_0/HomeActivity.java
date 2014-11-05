package com.TimothyShuang.notesshare.notesshare_1_0_0;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HomeActivity extends ActionBarActivity {
    private ImageView mImageView;
    private ShowDialogUtils mShowDialogUtils;
    private AlertDialog image_Alert;
    private String photoName;
    private static final String PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera";

    private static int REQUEST_CODE_GETIMAGE_FromGallery = 0;
    private static int REQUEST_CODE_GETIMAGE_FromCamera = 1;

    public static MyView myview;
    private LinearLayout viewlayout;
    private ImageButton undo_up,redo, file_save, size_change,color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView)findViewById(R.id.id_imageView);
        mShowDialogUtils = new ShowDialogUtils(this);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        myview = new MyView(this, null, width, height);
        viewlayout = (LinearLayout)findViewById(R.id.viewlayout);
        viewlayout.addView(myview);



        //Undo button
        undo_up = (ImageButton)findViewById(R.id.up);
        undo_up.setOnClickListener(new Undo_Up_OnClickListener());

        //clear button
        redo = (ImageButton)findViewById(R.id.clear);
        redo.setOnClickListener(new Redo_OnClickListener());

        //save button
        file_save = (ImageButton)findViewById(R.id.save);
        file_save.setOnClickListener(new Save_OnClickListener());

        //size change button
        size_change = (ImageButton)findViewById(R.id.size);
        size_change.setOnClickListener(new size_OnClickListener());

        //size change button
        color = (ImageButton)findViewById(R.id.colorman);
        color.setOnClickListener(new color_OnClickListener());

    }

    private class Undo_Up_OnClickListener implements View.OnClickListener{//Undo button
        public void onClick(View v){
            try {
                myview.undo_up();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class Redo_OnClickListener implements View.OnClickListener{//clear button
        public void onClick(View v){

            AlertDialog.Builder Clear_Alert = new AlertDialog.Builder(v.getContext());//this is create a pop up message
            Clear_Alert.setTitle("Erase Everything?");// this is the title of the message
            Clear_Alert.setMessage("Are you sure you want to erase notes and clear the screen?");//This is the body of the message

            Clear_Alert.setPositiveButton("Yes", new DialogInterface.OnClickListener(){// this is the button that is created. It is called Yes
                public void onClick(DialogInterface dialog, int which){
                    myview.redo();
                }
            });
            Clear_Alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){// this is the button that is created. It is called Cancel
                public void onClick(DialogInterface dialog, int which){

                    dialog.cancel();//this will exit the pop up message
                }
            });
            Clear_Alert.show();
        }
    }

    private class Save_OnClickListener implements View.OnClickListener{//save Button
        public void onClick(final View view){

            AlertDialog.Builder Save_Alert = new AlertDialog.Builder(view.getContext());
            Save_Alert.setTitle("Note Saving");
            Save_Alert.setMessage("Are you sure you want to save notes to device Gallery?");

            Save_Alert.setPositiveButton("Yes!!", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){

                    myview.setDrawingCacheEnabled(true);// make a non-scaled bitmap representing this view or null if cache is disabled.

                    String check = myview.file_save(view.getContext());//this accesses the file save method in my view

                    myview.destroyDrawingCache();//Destroy the drawing cache so that any future drawings saved won't use the existing cache

                    if(check != null){// this will check to see if the notes were really saved or not
                        Toast it_works = Toast.makeText(getApplicationContext(),
                                "Notes saved to Gallery!", Toast.LENGTH_LONG);
                        it_works.show();
                    }
                    else if(check == null){
                        Toast not_working = Toast.makeText(getApplicationContext(),
                                "!!!WARNING DID NOT SAVE NOTES!!!", Toast.LENGTH_LONG);
                        not_working.show();
                    }


                }
            });
            Save_Alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){

                    dialog.cancel();
                }
            });
            Save_Alert.show();
        }
    }


    private class size_OnClickListener implements View.OnClickListener{//size button
        public void onClick(View v){

            final Dialog penSize_Dialog = new Dialog(v.getContext());
            penSize_Dialog.setTitle("Choose Pen size:");//set the title
            penSize_Dialog.setContentView(R.layout.size_change);//set the layout

            ImageButton fin_tip = (ImageButton)penSize_Dialog.findViewById(R.id.fine_tip);

            fin_tip.setOnClickListener(new View.OnClickListener() {//listen for clicks on the three size buttons, starting with the small one:
                @Override
                public void onClick(View v) {

                    myview.size_change(1);
                    penSize_Dialog.dismiss();
                }
            });

            ImageButton norm_tip = (ImageButton)penSize_Dialog.findViewById(R.id.norm_tip);

            norm_tip.setOnClickListener(new View.OnClickListener() {//listen for clicks on the three size buttons, starting with the small one:
                @Override
                public void onClick(View v) {

                    myview.size_change(5);
                    penSize_Dialog.dismiss();
                }
            });

            ImageButton big_tip = (ImageButton)penSize_Dialog.findViewById(R.id.big);

            big_tip.setOnClickListener(new View.OnClickListener() {//listen for clicks on the three size buttons, starting with the small one:
                @Override
                public void onClick(View v) {

                    myview.size_change(10);
                    penSize_Dialog.dismiss();
                }
            });

            Button custom_tip = (Button)penSize_Dialog.findViewById(R.id.custom_button);

            custom_tip.setOnClickListener(new View.OnClickListener() {//listen for clicks on the three size buttons, starting with the small one:
                @Override
                public void onClick(View v) {

                    final Dialog customSize_Dialog = new Dialog(v.getContext());
                    customSize_Dialog.setTitle("Choose Pen size:");//set the title
                    customSize_Dialog.setContentView(R.layout.custom_size);//set the layout

                    final EditText et = (EditText) customSize_Dialog.findViewById(R.id.editText);

                    Button change_tip = (Button)customSize_Dialog.findViewById(R.id.change);
                    change_tip.setOnClickListener(new View.OnClickListener() {//listen for clicks on the three size buttons, starting with the small one:
                        @Override
                        public void onClick(View v) {
                            int size = Integer.parseInt(et.getText().toString());
                            myview.size_change(size);
                            customSize_Dialog.dismiss();
                        }
                    });
                    penSize_Dialog.dismiss();
                    customSize_Dialog.show();
                }
            });
            penSize_Dialog.show();

        }
    }

    private class color_OnClickListener implements View.OnClickListener{//color button
        public void onClick(View v){

            final Dialog color_Dialog = new Dialog(v.getContext());
            color_Dialog.setTitle("Choose a Color");//set the title
            color_Dialog.setContentView(R.layout.color_fragment);//set the layout


            ImageButton Color1 = (ImageButton)color_Dialog.findViewById(R.id.one);

            Color1.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFF660000);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color2 = (ImageButton)color_Dialog.findViewById(R.id.two);

            Color2.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFFFF0000);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color3 = (ImageButton)color_Dialog.findViewById(R.id.three);

            Color3.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFFFF6600);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color4 = (ImageButton)color_Dialog.findViewById(R.id.four);//line two

            Color4.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFF0000FF);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color5 = (ImageButton)color_Dialog.findViewById(R.id.five);

            Color5.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFF990099);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color6 = (ImageButton)color_Dialog.findViewById(R.id.six);

            Color6.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFFFF6666);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color7 = (ImageButton)color_Dialog.findViewById(R.id.seven);

            Color7.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFF787878);
                    color_Dialog.dismiss();
                }
            });
            ImageButton Color8 = (ImageButton)color_Dialog.findViewById(R.id.eight);

            Color8.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFF000000);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color9 = (ImageButton)color_Dialog.findViewById(R.id.nine);

            Color9.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFF009900);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color10 = (ImageButton)color_Dialog.findViewById(R.id.ten);

            Color10.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFF009999);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color11 = (ImageButton)color_Dialog.findViewById(R.id.eleven);

            Color11.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFFFFFFFF);
                    color_Dialog.dismiss();
                }
            });

            ImageButton Color12 = (ImageButton)color_Dialog.findViewById(R.id.twelve);

            Color12.setOnClickListener(new View.OnClickListener() {//This is the color Button
                @Override
                public void onClick(View v) {

                    myview.color_change(0xFFFFCC00);
                    color_Dialog.dismiss();
                }
            });

            color_Dialog.show();// this will show my color dialog
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        ActionBar actionbar = getActionBar();
        actionbar.setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(R.drawable.actionbar_background));
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Undo_Up_OnClickListener undor = new Undo_Up_OnClickListener();
        size_OnClickListener sizer = new size_OnClickListener();
        Save_OnClickListener saver = new Save_OnClickListener();
        Redo_OnClickListener clearer = new Redo_OnClickListener();
        color_OnClickListener colorer = new color_OnClickListener();

        int id = item.getItemId();
        if (id == R.id.id_image) {
            //AlertDialog.Builder image_Alert = new AlertDialog.Builder(this);
            //image_Alert.setTitle(R.string.choose_image);
            //image_Alert.setIcon(R.drawable.picture);
            //image_Alert.setIcon(R.drawable.camera);
            /*image_Alert.setSingleChoiceItems(new String[]{"Gallery", "Camera"}, 0,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );*/
            //image_Alert.setItems(new String[] { "Gallery", "Camera" }, null);
            /*String[] category_name = new String[] { "Gallery", "Camera" };
            Integer[] category_img = new Integer[]{R.drawable.picture, R.drawable.camera};
            List<ItemCategoryModel> mList = new ArrayList<ItemCategoryModel>();
            for(int i=0;i<category_img.length;i++)
                mList.add(new ItemCategoryModel(category_img[i],category_name[i]));
            ImageAdapter mAdapter = new ImageAdapter(this,mList);

            LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View mView = inflater.inflate(R.layout.image_dialog,(ViewGroup) findViewById(R.id.id_image_dialog));

            ListView mListView = (ListView)mView.findViewById(R.id.id_image_dialog);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new MyOnItemClickListener());
            AlertDialog.Builder image_Alert = new AlertDialog.Builder(this).setView(mListView);
            image_Alert.setTitle(R.string.choose_image);
            image_Alert.setIcon(R.drawable.image);
            image_Alert.show();*/

            String[] category_name = new String[] { "Gallery", "Camera" };
            Integer[] category_img = new Integer[]{R.drawable.picture, R.drawable.camera};
            ListView mListView = new ListView(this);
            mListView.setAdapter(getAdapter(category_name, category_img));
            mListView.setOnItemClickListener(new MyOnItemClickListener());
            image_Alert = new AlertDialog.Builder(this).setView(mListView).create();
            image_Alert.setTitle(R.string.choose_image);
            //image_Alert.setIcon(R.drawable.image);
            image_Alert.show();
            return true;
        }
        else if (id == R.id.id_share) {
            mShowDialogUtils.shareNotes();
            return true;
        }
        else if (id == R.id.id_update) {
            mShowDialogUtils.versionUpdate();
            return true;
        }
        else  if (id == R.id.id_about) {
            mShowDialogUtils.showAboutUs();
            return true;
        }
        else if (id == R.id.id_feedback) {
            mShowDialogUtils.writeToUs();
            return true;
        }
        else if(id == R.id.colorify){colorer.onClick(this.viewlayout); return true;}// color

        else if (id == R.id.id_pen) {sizer.onClick(this.viewlayout); return true;}// change size

        else if(id == R.id.id_undo){undor.onClick(findViewById(R.id.up)); return true;}// undo

        else if(id == R.id.id_redo){ myview.undo_down(); return true;}// redo

        else if(id == R.id.id_save){saver.onClick(this.viewlayout); return true;}// save

        else if(id == R.id.clear){clearer.onClick(this.viewlayout); return true;}// Clear


        return super.onOptionsItemSelected(item);
    }

    private SimpleAdapter getAdapter(String[] names,Integer[] images){
        ArrayList<HashMap<String, Object>> items=getData(names, images);
        SimpleAdapter adapter=new SimpleAdapter(
                this,
                items,
                R.layout.image_item,
                new String[]{"name","image"},
                new int[]{R.id.id_item_name, R.id.id_item_img});
        return adapter;
    }

    private ArrayList<HashMap<String, Object>> getData(String[] names,Integer[] images){
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
        for(int i=0;i<names.length;i++){
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("name", names[i]);
            item.put("image", images[i]);
            items.add(item);
        }
        return items;
    }

    class MyOnItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            image_Alert.dismiss();
            switch(arg2){
                case 0:
                    Intent intent_gallery = new Intent(Intent.ACTION_GET_CONTENT);
                    intent_gallery.addCategory(Intent.CATEGORY_OPENABLE);
                    intent_gallery.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent_gallery, "From Gallery"),
                            REQUEST_CODE_GETIMAGE_FromGallery);
                    break;
                case 1:
                    /*By myself
                    Intent intent_camera = new Intent();
                    intent_camera.setClass(this, CameraActivity.class);
                    startActivity(intent_camera);*/

                    //Specify open system camera Action
                    Intent intent_camera = new Intent();
                    intent_camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_camera.addCategory(Intent.CATEGORY_DEFAULT);
                    new DateFormat();
                    photoName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance()) + ".jpg";
                    Uri imageUri = Uri.fromFile(new File(PATH, photoName));
                    intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent_camera, REQUEST_CODE_GETIMAGE_FromCamera);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1){
            return;
        }
        switch (requestCode) {
            case 0:
                //Use ContentProvider and Uri to get photo
                ContentResolver resolver = getContentResolver();
                Uri imgUri = data.getData();
                Bitmap mBitmap = null;
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(resolver,imgUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Adjust the size
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                float scale = mBitmap.getWidth() / (float) dm.widthPixels;
                Bitmap newBitMap = null;
                if (scale > 1) {
                    newBitMap = zoomBitmap(mBitmap, mBitmap.getWidth()
                            / scale, mBitmap.getHeight() / scale);
                    mBitmap.recycle();
                    mImageView.setImageBitmap(newBitMap);
                    myview.B_ground(myview.getmCanvas(),newBitMap);
                }
                else{ mImageView.setImageBitmap(mBitmap); myview.B_ground(myview.getmCanvas(),mBitmap);}
                break;
            case 1:
                //Use bundle to get photo -- it is a thumbnail so not clear
                //Bundle bundle = data.getExtras();
                //Bitmap mBitmap_2 = (Bitmap) bundle.get("data");

                String imageFilePath = PATH + "/" + photoName;
                Bitmap mBitmap_2 = BitmapFactory.decodeFile(imageFilePath);
                //Adjust the size
                DisplayMetrics dm_2 = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm_2);
                float scale_2 = mBitmap_2.getWidth() / (float) dm_2.widthPixels;
                Bitmap newBitMap_2 = null;
                if (scale_2 > 1) {
                    newBitMap_2 = zoomBitmap(mBitmap_2, mBitmap_2.getWidth()
                            / scale_2, mBitmap_2.getHeight() / scale_2);
                    mBitmap_2.recycle();
                    mImageView.setImageBitmap(newBitMap_2);
                    myview.B_ground(myview.getmCanvas(),newBitMap_2);
                }
                else {mImageView.setImageBitmap(mBitmap_2); myview.B_ground(myview.getmCanvas(),mBitmap_2);}
                break;
            default:
                break;
        }
    }

    public Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
}
