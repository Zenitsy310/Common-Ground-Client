package com.ark_das.springclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.model.Role;
import com.ark_das.springclient.model.User;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;
import android.widget.Filterable;

public class UserAdapter extends RecyclerView.Adapter<UserHolder>{

    private List<User> userList;
    private List<Role> roleList;

    private List<User> userListFull;


    public UserAdapter(List<User> userList, List<Role> roleList){
        this.userList = userList != null ? userList : new ArrayList<>();
        this.roleList = roleList != null ? roleList : new ArrayList<>();
        this.userListFull = new ArrayList<>(this.userList);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user_items, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {

        if(userList == null || userList.isEmpty() || position < 0 || position >userList.size()){
            bindEmptyView(holder);
            return;
        }

        User user = userList.get(position);
        if(user == null){
            bindEmptyView(holder);
            return;
        }
        holder.first_name.setText(
                user.getFirst_name() != null? user.getFirst_name():"No name");
        holder.last_name.setText(
                user.getLast_name() != null? user.getLast_name():"No last name");
        holder.email.setText(
                user.getEmail() != null? user.getEmail(): "No email");
        /*holder.role.setText(String.valueOf(
                roleList.get(user.getRole_id()).getName() != null?
                        roleList.get(user.getRole_id()).getName():"No role"));*/
        holder.role.setText(getRoleName(user.getRole_id()));
    }

    private String getRoleName(int roleId){

        for(Role role : roleList){
            if(role.getId() == roleId) {
                return role.getName() != null? role.getName() : "Role " + roleId;
            }
        }

        return "Unknown role";
    }

    private void bindEmptyView(UserHolder holder) {
        holder.first_name.setText("No data");
        holder.last_name.setText("");
        holder.email.setText("");
        holder.role.setText("");
    }

    @Override
    public int getItemCount() {
        return userList != null?userList.size() : 0;
    }

    public Filter getFilter(){
        return userFilter;
    }

    private Filter userFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        }
    };
}
