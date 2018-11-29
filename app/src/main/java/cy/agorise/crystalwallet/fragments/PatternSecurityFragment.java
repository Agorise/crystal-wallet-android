package cy.agorise.crystalwallet.fragments;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.util.ChildViewPager;
import cy.agorise.crystalwallet.util.PasswordManager;

/**
 * Created by xd on 1/18/18.
 */

public class PatternSecurityFragment extends Fragment {

    @BindView(R.id.patternLockView)
    PatternLockView patternLockView;
    @BindView(R.id.tvPatternText)
    TextView tvPatternText;

    /*
     * Contains the ChildViewPager to block the viewpager when the user is using the pattern control
     * */
    private ChildViewPager childViewPager;

    private PatternLockViewListener actualPatternListener;
    private String patternEntered;




    public PatternSecurityFragment() {
        // Required empty public constructor
    }

    public static PatternSecurityFragment newInstance() {
        PatternSecurityFragment fragment = new PatternSecurityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pattern_security, container, false);
        ButterKnife.bind(this, v);

        showNewPatternUI();

        return v;
    }

    public String patternToString(List<PatternLockView.Dot> pattern){
        String patternString = "";
        for (PatternLockView.Dot nextDot : pattern){
            patternString = patternString+(nextDot.getRow()*3+nextDot.getColumn());
        }

        return patternString;
    }

    public void setChildViewPager(ChildViewPager childViewPager) {
        this.childViewPager = childViewPager;
    }


    public void removePatternListener(){
        if (actualPatternListener != null){
            patternLockView.removePatternLockListener(actualPatternListener);
            actualPatternListener = null;
        }
    }

    public void showNewPatternUI(){
        removePatternListener();
        patternLockView.clearPattern();
        tvPatternText.setTextColor(Color.WHITE);
        tvPatternText.setText(getActivity().getResources().getString(R.string.Enter_new_pattern));

        actualPatternListener = new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                patternEntered = patternToString(pattern);
                showConfirmPatternUI();
            }

            @Override
            public void onCleared() {

            }
        };
        patternLockView.addPatternLockListener(actualPatternListener);
    }

    public void showConfirmPatternUI(){
        removePatternListener();
        patternLockView.clearPattern();
        patternLockView.requestFocus();
        tvPatternText.setText(getActivity().getResources().getString(R.string.Confirm_new_pattern));

        actualPatternListener = new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (patternEntered.equals(patternToString(pattern))){
                    savePattern(patternEntered);
                }
                else{
                    resetPattern();
                }
            }

            @Override
            public void onCleared() {

            }
        };
        patternLockView.addPatternLockListener(actualPatternListener);
    }

    private void resetPattern(){

        /*
         * Show error
         * */
        tvPatternText.setText(getActivity().getResources().getString(R.string.Incorrect_pattern));
        tvPatternText.setTextColor(Color.RED);
        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {

                                      getActivity().runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {

                                              t.cancel();
                                              showNewPatternUI();
                                          }
                                      });
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                1000,
                //Set the amount of time between each execution (in milliseconds)
                1000);
    }

    public void savePattern(String pattern){
        String patternEncripted = PasswordManager.encriptPassword(pattern);
        CrystalSecurityMonitor.getInstance(null).setPatternEncrypted(patternEncripted);
        /*CrystalSecurityMonitor.getInstance(null).callPasswordRequest(this.getActivity(), new OnResponse() {
            @Override
            public void onSuccess() {

                Log.i("onSuccess","onSuccess");
                Toast.makeText(getActivity(), "onSuccess", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailed() {
                Log.i("onFailed","onFailed");
                Toast.makeText(getActivity(), "onFailed", Toast.LENGTH_LONG).show();
            }
        });*/

        /*
         * Show success
         * */
        tvPatternText.setText(getActivity().getResources().getString(R.string.Pattern_set_correctly));
        tvPatternText.setTextColor(Color.GREEN);
        final Timer t_ = new Timer();
        t_.scheduleAtFixedRate(new TimerTask() {

                                   @Override
                                   public void run() {

                                       getActivity().runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {

                                               t_.cancel();
                                               showNewPatternUI();
                                           }
                                       });
                                   }

                               },
                //Set how long before to start calling the TimerTask (in milliseconds)
                1000,
                //Set the amount of time between each execution (in milliseconds)
                1000);
    }
}
