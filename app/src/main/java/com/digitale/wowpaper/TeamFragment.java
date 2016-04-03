package com.digitale.wowpaper;

/**
 * Fragment for displaying match info
 * Created by Rich on 22/03/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Match info Fragment.
 */
public class TeamFragment extends Fragment implements FragmentNotifier {
    View rootView = null;

    public TeamFragment() {
    }

    @Override
    public void fragmentVisible() {
        //do animation here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_team, container, false);
        super.onCreate(savedInstanceState);
        String tag = this.getTag();
        tag = tag.substring(tag.lastIndexOf(':') + 1);

        ListView mHomeListView = (ListView) rootView.findViewById(R.id.teamListView);
        mHomeListView.setAdapter(MainActivity.mPlayerAdapter);
        invalidateTeamView((MainActivity) getActivity());

        return rootView;
    }

    public void invalidateTeamView(MainActivity activity) {
        if (rootView != null) {

//            TextView textTeamName = (TextView) rootView.findViewById(R.id.cTeamName);
//            TextView textValue = (TextView) rootView.findViewById(R.id.cSquadValue);
//
//            TextView textPoints = (TextView) rootView.findViewById(R.id.cWins);
//            if(MainActivity.mDatabase.team.getShortName()!="null") {
//                textTeamName.setText(MainActivity.mDatabase.team.getName() + " (" +
//                        MainActivity.mDatabase.team.getShortName() + ")");
//            }else{
//                textTeamName.setText(MainActivity.mDatabase.team.getName());
//            }
//            System.out.println("SQUAD VALUE"+MainActivity.mDatabase.team.getSquadMarketValue());
//            if(MainActivity.mDatabase.team.getSquadMarketValue()!="null") {
//                textValue.setText(MainActivity.mDatabase.team.getSquadMarketValue());
//            }else {
//                textValue.setText("Unknown");
//            }
//            int lGoals=MainActivity.mDatabase.getLeague().getStandings().get(MainActivity.mTeamIndex).getGoals();
//            int lGoalsAgainst =MainActivity.mDatabase.getLeague().getStandings().get(MainActivity.mTeamIndex).getGoalsAgainst();
//            int lGoalsDiff=Math.abs(MainActivity.mDatabase.getLeague().getStandings().get(MainActivity.mTeamIndex).getGoalDifference());
//            int lWins=MainActivity.mDatabase.getLeague().getStandings().get(MainActivity.mTeamIndex).getWins();
//            int lDraws=MainActivity.mDatabase.getLeague().getStandings().get(MainActivity.mTeamIndex).getDraws();
//            int lLosses=MainActivity.mDatabase.getLeague().getStandings().get(MainActivity.mTeamIndex).getLosses();
//            textPoints.setText(String.valueOf( MainActivity.mDatabase.getLeague().getStandings().get(MainActivity.mTeamIndex).getPoints()));
//            PieChart goalsChart =(PieChart)rootView.findViewById(R.id.chartGoals);
//          setChart(lGoals, lGoalsAgainst, lGoalsDiff, goalsChart,"Goals");
//            PieChart winsChart =(PieChart)rootView.findViewById(R.id.chartWins);
//            setChart(lWins,lLosses,lDraws,winsChart,"Results");
        }

    }
    public void setChart(int goals,int goalsAgainst,int goalDiff,PieChart chart,String title){
        ArrayList <Entry> dataSet=new ArrayList<Entry>();
        //set data
        Entry c1=new Entry(goals,0);
        Entry c2=new Entry(goalsAgainst,1);
        Entry c3=new Entry(goalDiff,2);
        dataSet.add(c1);
        dataSet.add(c2);
        dataSet.add(c3);
        PieDataSet goalData=new PieDataSet(dataSet,"");
        ArrayList<String> xVals = new ArrayList<String>();
       if(title.equals("Goals")) {
           xVals.add("For");
           xVals.add("Against");
           xVals.add("Difference");
       }else{
           xVals.add("Wins");
           xVals.add("Losses");
           xVals.add("Draws");
       }
        //set appearence
        chart.setDrawSliceText(false);
        chart.setDescription("");
        chart.setCenterText(title);
        goalData.setValueTextSize(15f);
        goalData.setColors(new int[]{R.color.green, R.color.red, R.color.yellow}, (MainActivity) getActivity());
        DecimalFormat format=new DecimalFormat("##");
        goalData.setValueFormatter(new IntegerFormatter());
        PieData pieData=new PieData(xVals,goalData);
        chart.animateXY(1000, 1000);
        chart.setData(pieData);
        chart.invalidate();
    }
}