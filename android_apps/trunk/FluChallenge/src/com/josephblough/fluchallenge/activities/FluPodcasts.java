package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.services.FluPodcastsFeedDownloaderService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class FluPodcasts extends FeedListActivity {
    
    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the Flu podcasts feed";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	ApplicationController app = (ApplicationController)getApplicationContext();
	if (app.fluPodcastsFeed != null && app.fluPodcastsFeed.items != null && app.fluPodcastsFeed.items.size() > 0)
	    done();
	else
	    loadFluPodcastsFeed();
    }
    
    private void loadFluPodcastsFeed() {
	Intent intent = new Intent(this, FluPodcastsFeedDownloaderService.class);
	intent.putExtra(FluPodcastsFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(ERROR_MSG);
			}
		    }
		}));
	startService(intent);

	progress = ProgressDialog.show(this, "", "Downloading Flu podcasts feed");
    }
    
    private void error(final String error) {
	if (progress != null)
	    progress.dismiss();
	
	Toast.makeText(this, error, Toast.LENGTH_LONG);
    }
    
    private void done() {
	if (progress != null)
	    progress.dismiss();
	
	ApplicationController app = (ApplicationController)getApplicationContext();
	RssFeedEntryAdapter adapter = new RssFeedEntryAdapter(app.fluPodcastsFeed.items);
	setListAdapter(adapter);
    }
}
