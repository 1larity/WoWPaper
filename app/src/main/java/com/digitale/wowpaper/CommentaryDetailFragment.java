package com.digitale.wowpaper;

/**
 * Fragment for displaying detailed commentary record
 * Created by Rich on 22/03/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Commentary Details Fragment.
 */
public class CommentaryDetailFragment extends Fragment implements FragmentNotifier {
    private View rootView = null;
    private int eventIndex=0;
    private int arraySize=0;
    public CommentaryDetailFragment() {
  }
    @Override
    public void fragmentVisible() {
        invalidateCommentaryDetailView((MainActivity) getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments()!=null) {
         arraySize = getArguments().getInt("listSize");
        }
        rootView = inflater.inflate(R.layout.fragment_commentary_detail, container, false);
        Button buttonPrevious = (Button) rootView.findViewById(R.id.buttonPrevious);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (eventIndex > 0) {
                    eventIndex--;
                }
                invalidateCommentaryDetailView((MainActivity) getActivity());
            }
        });
        Button buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (eventIndex < arraySize - 1) {
                    eventIndex++;
                }
                invalidateCommentaryDetailView((MainActivity) getActivity());
            }
        });
        return rootView;
    }
    public  void invalidateCommentaryDetailView(MainActivity activity) {
        TextView textEvent = (TextView) rootView.findViewById(R.id.cEvent);
        TextView textHeading = (TextView) rootView.findViewById(R.id.cHeading);
        TextView textDescription = (TextView) rootView.findViewById(R.id.cDescription);
        TextView textTime = (TextView) rootView.findViewById(R.id.cTime);
        textEvent.setText(activity.mCommentaryList.get(eventIndex).getType());
        textHeading.setText(activity.mCommentaryList.get(eventIndex).getHeading());
        textDescription.setText(activity.mCommentaryList.get(eventIndex).getDescription());
        textTime.setText(activity.mCommentaryList.get(eventIndex).getTime());
    }
    public void setArraySize(int arraySize){
        this.arraySize=arraySize;
        invalidateCommentaryDetailView((MainActivity) getActivity());
    }


}
