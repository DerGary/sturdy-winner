package com.example.student.gefriertruhapp.DetailFragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/**
 * Created by student on 21.12.15.
 */
public class ShelfDetailFragment extends TitleFragment {
    private ShelfItem item;
    private View rootView;
    private Button notificationButton;
    private EditText name, quantity, minquantity;
    private TextView notificationDate;
    DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm - dd.MM.yyyy");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shelf_detail, container, false);

        notificationButton = (Button)rootView.findViewById(R.id.shelf_detail_notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetNotificationClicked();
            }
        });

        name = (EditText) rootView.findViewById(R.id.shelf_detail_name);
        quantity = (EditText)rootView.findViewById(R.id.shelf_detail_quantity);
        notificationDate = (TextView) rootView.findViewById(R.id.shelf_detail_notification_date);
        minquantity = (EditText)rootView.findViewById(R.id.shelf_detail_minquantity);

        setViewData();

        return rootView;
    }

    private void setViewData(){
        quantity.setText(String.valueOf(item.getQuantity()));
        minquantity.setText(String.valueOf(item.getMinQuantity()));
        name.setText(item.getName());
        if(item.getNotificationDate() != null) {
            notificationDate.setText(formatter.print(item.getNotificationDate()));
        }
    }
    private void getViewData(){
        item.setQuantity(Integer.parseInt(quantity.getText().toString()));
        item.setName(name.getText().toString());
        item.setMinQuantity(Integer.parseInt(minquantity.getText().toString()));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getTitle());
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_item_delete){
            DataBaseSingleton.getInstance().deleteShelfItem(this.item);
            DataBaseSingleton.getInstance().saveDataBase(getActivity().getBaseContext());
            //Todo: delete shelfitem and navigate Back
            ((Dashboard) getActivity()).onBackPressed();
        }else if(id == R.id.menu_item_save){
            getViewData();
            DataBaseSingleton.getInstance().saveShelfItem(this.item);
            DataBaseSingleton.getInstance().saveDataBase(getActivity().getBaseContext());
            //Todo: Save and navigate Back
            ((Dashboard) getActivity()).onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setData(ShelfItem item){
        this.item = item;
    }

    @Override
    public String getTitle() {
        return "Lager";
    }

    private void onSetNotificationClicked() {
        showDatePicker(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                showTimePicker(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        DateTime notificationDateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);


                        item.setNotificationDate(notificationDateTime);

                        TextView text = (TextView) rootView.findViewById(R.id.shelf_detail_notification_date);
                        text.setText(formatter.print(notificationDateTime));
                    }
                });
            }
        });
    }

    private void showDatePicker(DatePickerDialog.OnDateSetListener listener) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
        dialog.show();
    }

    private void showTimePicker(TimePickerDialog.OnTimeSetListener listener) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
        dialog.show();
    }
}
