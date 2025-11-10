package com.ark_das.springclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.model.Role;
import com.ark_das.springclient.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {

    private List<User> userList;
    private List<Role> roleList;
    private List<User> userListFull; // для поиска

    public UserAdapter(List<User> userList, List<Role> roleList) {
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
        // Защита от null и выхода за границы
        if (userList == null || userList.isEmpty() || position < 0 || position >= userList.size()) {
            bindEmptyView(holder);
            return;
        }

        User user = userList.get(position);
        if (user == null) {
            bindEmptyView(holder);
            return;
        }

        // Заполняем данные пользователя
        holder.first_name.setText(user.getFirst_name() != null ? user.getFirst_name() : "No name");
        holder.last_name.setText(user.getLast_name() != null ? user.getLast_name() : "No last name");
        holder.email.setText(user.getEmail() != null ? user.getEmail() : "No email");

        // Получаем название роли
        String roleName = getRoleName(user.getRole_id());
        holder.role.setText(roleName);
    }

    private String getRoleName(int roleId) {
        if (roleList == null || roleList.isEmpty()) {
            return "Role: " + roleId;
        }

        // Ищем роль по ID
        for (Role role : roleList) {
            if (role.getId() == roleId) {
                return role.getName() != null ? role.getName() : "Role " + roleId;
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
        return userList != null ? userList.size() : 0;
    }

    // Метод для обновления данных
    public void updateData(List<User> newUserList, List<Role> newRoleList) {
        this.userList = newUserList != null ? newUserList : new ArrayList<>();
        this.roleList = newRoleList != null ? newRoleList : new ArrayList<>();
        this.userListFull = new ArrayList<>(this.userList);
        notifyDataSetChanged();
    }

    // Метод только для пользователей
    public void setUsers(List<User> userList) {
        this.userList = userList != null ? userList : new ArrayList<>();
        this.userListFull = new ArrayList<>(this.userList);
        notifyDataSetChanged();
    }

    // Метод только для ролей
    public void setRoles(List<Role> roleList) {
        this.roleList = roleList != null ? roleList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Опционально: добавьте поиск/фильтрацию
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (User user : userListFull) {
                    if (user.getFirst_name() != null && user.getFirst_name().toLowerCase().contains(filterPattern) ||
                            user.getLast_name() != null && user.getLast_name().toLowerCase().contains(filterPattern) ||
                            user.getEmail() != null && user.getEmail().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}