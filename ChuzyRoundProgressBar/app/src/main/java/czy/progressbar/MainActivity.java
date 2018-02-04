package czy.progressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

//    private ChuzyProgressBar progressBar1;
    private ChuzyProgressBar progress;
    private ChuzyProgressBar progress2;
    private Button btn;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ChuzyProgressBar) findViewById(R.id.roundProgressBar1);
        progress2 = (ChuzyProgressBar) findViewById(R.id.roundProgressBar2);
        btn = (Button) findViewById(R.id.rest);
        btn2 = (Button) findViewById(R.id.rest2);
        progress.setMax(100);
        progress2.setMax(100);
        progress.isShowPoint(true);
        progress2.isShowPoint(false);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int value = random.nextInt(100);
                progress.setProgressWithAnimation(value);

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int value = random.nextInt(100);
                progress2.setProgressWithAnimation(value);

            }
        });

    }


    private void test2(){

    }
}
