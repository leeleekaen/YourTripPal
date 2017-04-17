package lshankarrao.travelatease1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class template_ttp_array_adapter extends ArrayAdapter<String> {
    final List<String> ttp_templateHeadings;
    private Context ctxt;

    public template_ttp_array_adapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.ttp_templateHeadings = objects;
        ctxt = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ScrapViewHolder holder;
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.templates_ttp_custom_row, parent, false);
            holder = new ScrapViewHolder();
            holder.heading = (TextView) row.findViewById(R.id.textViewTTPtemplateHeading);
            row.setTag(holder);
        } else {
            Log.d("lux", "Row NOT null; reusing it!");
            holder = (ScrapViewHolder) row.getTag();
        }

        holder.heading.setText(ttp_templateHeadings.get(position).toString());

        return row;
    }

    public class ScrapViewHolder {
        TextView heading;
    }
}