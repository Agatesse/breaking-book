package com.projects.breakingbook.persistence.mapper;

import com.projects.breakingbook.business.entity.RoleName;
import com.projects.breakingbook.business.entity.User;
import com.projects.breakingbook.business.entity.Wishlist;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WishlistMapper implements RowMapper<Wishlist> {
    @Override
    public Wishlist mapRow(final ResultSet resultSet, final int i) throws SQLException {
        final User user = User.builder()
                .id(resultSet.getLong("breaking_book_user_id"))
                .username(resultSet.getString("breaking_book_user_username"))
                .avatar(resultSet.getString("breaking_book_user_avatar"))
                .email(resultSet.getString("breaking_book_user_email"))
                .password(resultSet.getString("breaking_book_user_password"))
                .build();
        final String role = (resultSet.getString("breaking_book_user_role"));
        user.setRole(RoleName.valueOf(role));

        return Wishlist.builder()
                .id(resultSet.getLong("wishlist_id"))
                .name(resultSet.getString("wishlist_name"))
                .user(user)
                .build();
    }
}