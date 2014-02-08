package cn.retech.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
  private final List<Fragment> dataSource;

  public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> dataSource) {
    super(fm);
    this.dataSource = dataSource;
  }

  public int getCount() {
    return dataSource != null ? dataSource.size() : 0;
  }

  @Override
  public Fragment getItem(int position) {
    return dataSource.get(position);
  }

}
