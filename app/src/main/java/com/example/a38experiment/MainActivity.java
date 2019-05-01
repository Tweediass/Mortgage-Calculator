package com.example.a38experiment;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private TextView textView5;
    private TextView textView12;
    private TextView textView13;
    private TextView textView14;
    private TextView textView15;
    private TextView textView16;
    private Button button1;
    private Button button2;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private Spinner spinner1;
    private Spinner spinner2;

    String price;//总价
    String loan;//按揭部分
    double totalPrice;//贷款总额
    int time = 0;//贷款总月数
    double comMonIntRate = 0;//商贷月利率
    double proMonIntRate = 0;//公积金月利率
    double comLoan = 0;//商贷
    double proFund = 0;//公积金
    boolean comLoanFlag = false;//是否商贷
    boolean proFundFlag = false;//是否公积金
    double monRepayment;//每月还款总额
    double totalRepayment;//还款总额
    double ratePayment;//利息总额
    double[][] rate = {{2017, 0.05, 0.0335}, {2018, 0.0495, 0.033}, {2019, 0.059, 0.0325}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();


        // 建立数据源
        String[] loanPeriod = getResources().getStringArray(R.array.loanPeriod);
        String[] benchmarkInterestRate = getResources().getStringArray(R.array.benchmarkInterestRate);

        // 建立Adapter并且绑定数据源并居中显示
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loanPeriod) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getView(position, convertView, parent));
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }

            @SuppressLint("ResourceAsColor")
            private View setCentered(View view) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.red));
                return view;
            }
        };
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, benchmarkInterestRate) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getView(position, convertView, parent));
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }

            private View setCentered(View view) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.red));
                return view;
            }
        };
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //绑定Adapter到控件
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);

        //初始化控件监听：计算贷款总额
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price = editText1.getText().toString();
                loan = editText2.getText().toString();
                totalPrice = Double.valueOf(price).doubleValue() * Double.valueOf(loan).doubleValue() / 100;
                textView5.setText("您的贷款总额为: " + totalPrice + "万元");
            }
        });

        //初始化控件监听：计算还款总额
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logic();
                if (radioButton1.isChecked()) {
                    Calculation1();
                } else if (radioButton2.isChecked()) {
                    Calculation2();
                }
                //输出您的贷款总额
                textView12.setText("您的贷款总额为: " + (double) Math.round(totalPrice * 100) / 100 + "万元");

                //输出每月还款金额
                monRepayment = comLoan + proFund;
                textView13.setText("每月还款金额为: " + (double) Math.round(monRepayment * 10000 * 100) / 100 + "元");

                //输出还款总额
                totalRepayment = monRepayment * time;
                textView16.setText("还款总额为: " + (double) Math.round(totalRepayment * 100) / 100 + "万元");

                //输出利息总额
                ratePayment = totalRepayment - totalPrice;
                textView15.setText("其中利息总额为: " + (double) Math.round(ratePayment * 100) / 100 + "万元");

                //输出还款总时间
                textView14.setText("还款总时间为: " + time + "月");
            }
        });
    }


    // 初始化控件
    private void findViews() {
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        textView5 = findViewById(R.id.textView5);
        textView12 = findViewById(R.id.textView12);
        textView13 = findViewById(R.id.textView13);
        textView14 = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);
        textView16 = findViewById(R.id.textView16);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
    }

    //业务逻辑
    private void logic() {
        time = Integer.valueOf(spinner1.getSelectedItem().toString().replace("年", "")).intValue() * 12;
        switch (spinner2.getSelectedItemPosition()) {
            case 0:
                if (checkBox1.isChecked()) {
                    comMonIntRate = rate[0][1] / 12;
                    comLoan = Double.valueOf(editText3.getText().toString()).doubleValue();
                    comLoanFlag = true;
                }
                if (checkBox2.isChecked()) {
                    proMonIntRate = rate[0][2] / 12;
                    proFund = Double.valueOf(editText4.getText().toString()).doubleValue();
                    proFundFlag = true;
                }
                break;
            case 1:
                if (checkBox1.isChecked()) {
                    comMonIntRate = rate[1][1] / 12;
                    comLoan = Double.valueOf(editText3.getText().toString()).doubleValue();
                    comLoanFlag = true;
                }
                if (checkBox2.isChecked()) {
                    proMonIntRate = rate[1][2] / 12;
                    proFund = Double.valueOf(editText4.getText().toString()).doubleValue();
                    proFundFlag = true;
                }
                break;
            case 2:
                if (checkBox1.isChecked()) {
                    comMonIntRate = rate[2][1] / 12;
                    comLoan = Double.valueOf(editText3.getText().toString()).doubleValue();
                    comLoanFlag = true;
                }
                if (checkBox2.isChecked()) {
                    proMonIntRate = rate[2][2] / 12;
                    proFund = Double.valueOf(editText4.getText().toString()).doubleValue();
                    proFundFlag = true;
                }
        }
    }

    //等额本息计算
    private void Calculation1() {
        //公式：每月还款额=贷款本金×[月利率×(1+月利率) ^ 还款月数]÷{[(1+月利率) ^ 还款月数]-1}

        //商贷计算
        if (comLoanFlag) {
            comLoan = comMonIntRate * Math.pow((1 + comMonIntRate), time) * comLoan / (Math.pow((1 + comMonIntRate), time) - 1);
        } else {
            comLoan = 0;
        }

        //公积金计算
        if (proFundFlag) {
            proFund = proMonIntRate * Math.pow((1 + proMonIntRate), time) * proFund / (Math.pow((1 + proMonIntRate), time) - 1);
        } else {
            proFund = 0;
        }
    }

    //等额本金计算
    private void Calculation2() {
        //商贷计算
        if (comLoanFlag) {
            double actMonRepayment = comLoan / 240;//每月还款本金
            double firRate = comLoan * comMonIntRate;//第一个月还利息
            double firRepayment = actMonRepayment + firRate;//第一个月还款总额
            double toPay = firRepayment * time;//实际偿还的金额
            double reduce = actMonRepayment * comMonIntRate * time * (time - 1) / 2;//所有月数所积累的减少的利息
            comLoan = (toPay - reduce) / time;
        } else {
            comLoan = 0;
        }

        //公积金计算
        if (proFundFlag) {
            double actMonRepayment = proFund / 240;//每月还款本金
            double firRate = proFund * proMonIntRate;//第一个月还利息
            double firRepayment = actMonRepayment + firRate;//第一个月还款总额
            double toPay = firRepayment * time;//实际偿还的金额
            double reduce = actMonRepayment * proMonIntRate * time * (time - 1) / 2;//所有月数所积累的减少的利息
            proFund = (toPay - reduce) / time;
        } else {
            proFund = 0;
        }
    }
}