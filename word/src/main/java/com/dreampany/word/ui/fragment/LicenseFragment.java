package com.dreampany.word.ui.fragment;

import android.os.Bundle;

import com.artitk.licensefragment.model.LicenseID;
import com.artitk.licensefragment.support.v4.RecyclerViewLicenseFragment;
import com.dreampany.frame.misc.ActivityScope;
import com.dreampany.frame.ui.fragment.BaseFragment;
import com.dreampany.frame.util.FragmentUtil;
import com.dreampany.word.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Hawladar Roman on 3/8/18.
 * Dreampany Ltd
 * dreampanymail@gmail.com
 */
@ActivityScope
public class LicenseFragment extends BaseFragment {

    @Inject
    public LicenseFragment() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_license;
    }

    @Override
    protected void onStartUi(@Nullable Bundle state) {
        ArrayList<Integer> licenseIds = new ArrayList<>();
        licenseIds.add(LicenseID.GSON);
        licenseIds.add(LicenseID.OKHTTP);
        licenseIds.add(LicenseID.RETROFIT);


        RecyclerViewLicenseFragment fragment = FragmentUtil.getFragment(this, R.id.fragment);
        fragment.addLicense(licenseIds);
    }

    @Override
    protected void onStopUi() {

    }
}
