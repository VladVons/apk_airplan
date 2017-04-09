package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityAbout extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
        LayoutInflater ltInflater = getLayoutInflater();

        String[] name = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис", "Костя", "Игорь" };
        int salary[] = { 13000, 10000, 13000, 13000, 10000, 15000, 13000, 8000 };

        int[] colors = new int[2];
        colors[0] = Color.parseColor("#559966CC");
        colors[1] = Color.parseColor("#55336699");

        for (int i = 0; i < name.length; i++) {
            View item = ltInflater.inflate(R.layout.frame_keyvalue, linLayout, false);

            TextView tvKey = (TextView) item.findViewById(R.id.tvKey);
            tvKey.setText(name[i]);

            TextView tvValue = (TextView) item.findViewById(R.id.tvValue);
            tvValue.setText(String.valueOf(salary[i]));

            item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            item.setBackgroundColor(colors[i % 2]);
            linLayout.addView(item);
        }
    }
}
