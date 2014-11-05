package com.TimothyShuang.notesshare.notesshare_1_0_0;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.inscription.ChangeLogDialog;

public class ShowDialogUtils {
	
	public static final String SUPPORT_EMAIL_RECEIVER = "notessharens@gmail.com";
	//password: TimothyShuang
	public static final String SUPPORT_EMAIL_SUBJECT = "[Feedback for NotesShare Android version]";
	
	private Context mContext;
	
	private String mClickedText = "";
	
	public ShowDialogUtils(Context context){
		mContext = context;
	}

    public void shareNotes() {

    }

    public void versionUpdate(){
        ChangeLogDialog changeLogDialog = new ChangeLogDialog(mContext);
        changeLogDialog.show();
    }

	public void showAboutUs() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
        AlertDialog alertDlg = LightAlertDialog.create(mContext);
        alertDlg.setTitle(null);
        alertDlg.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.close), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {

			}
		});

        View view = inflater.inflate(R.layout.about_us, null);

        TextView versionText = (TextView)view.findViewById(R.id.tv_version);
        String versionName = "";
        try {
            versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0 ).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        versionText.setText("NotesShare Android " + versionName);

        alertDlg.setView(view);

        alertDlg.show();
	}
	
	public void writeToUs(){
		Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        
        String[] emailReceiver = new String[]{ SUPPORT_EMAIL_RECEIVER };
        String emailSubject = SUPPORT_EMAIL_SUBJECT;
        String emailBody = "";
		try {
			String densityStr = "Unknown";
			int density= mContext.getResources().getDisplayMetrics().densityDpi;
			switch (density) {
				case DisplayMetrics.DENSITY_LOW:
					densityStr = "LDPI";
				    break;
				case DisplayMetrics.DENSITY_MEDIUM:
					densityStr = "MDPI";
				    break;
				case DisplayMetrics.DENSITY_HIGH:
					densityStr = "HDPI";
				    break;
				case DisplayMetrics.DENSITY_XHIGH:
					densityStr = "XHDPI";
				    break;
				default:
					break;
			}
			
			String screenSizeStr = "Unknown";
			int screenSize = mContext.getResources().getConfiguration().screenLayout &
			        Configuration.SCREENLAYOUT_SIZE_MASK;
			switch (screenSize) {
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	screenSizeStr = "Large screen";
			        break;
			    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			    	screenSizeStr = "Normal screen";
			        break;
			    case Configuration.SCREENLAYOUT_SIZE_SMALL:
			    	screenSizeStr = "Small screen";
			        break;
			    default:
			}
			
			PackageInfo info = mContext.getPackageManager().getPackageInfo("com.TimothyShuang.notesshare.notesshare_1_0_0", 0);
			emailBody = String.format("\n\n\n\nApp Version: %s\nDevice Brand: %s\nOS Version: %s\n" +
					"Screen Density: %s\nScreen Size: %s", 
					info.versionName, android.os.Build.BRAND, android.os.Build.VERSION.RELEASE, 
					densityStr, screenSizeStr);
		} catch (PackageManager.NameNotFoundException e) {
			
		} catch (Exception e) {
			
		}

        email.putExtra(Intent.EXTRA_EMAIL, emailReceiver);
        email.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        email.putExtra(Intent.EXTRA_TEXT, emailBody);
        
        try {
        	mContext.startActivity(email);
        }
        catch (Exception e) {}
	}

}