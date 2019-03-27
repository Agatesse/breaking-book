package com.projects.breakingbook.persistence.repository.implementation;

import com.projects.breakingbook.persistence.entity.Book;
import com.projects.breakingbook.persistence.entity.Collection;
import com.projects.breakingbook.persistence.entity.Friend;
import com.projects.breakingbook.persistence.entity.mapper.CollectionMapExtractor;
import com.projects.breakingbook.persistence.entity.mapper.FriendMapExtractor;
import com.projects.breakingbook.persistence.entity.mapper.FriendMapper;
import com.projects.breakingbook.persistence.repository.FriendRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class FriendRepositoryImpl implements FriendRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String INSERT = "INSERT INTO friend(friend_name, friend_avatar, friend_reader) VALUES (?, ?, ?)";
    private final String SELECT_ALL = "SELECT * FROM friend INNER JOIN reader r ON friend.friend_reader = r.reader_id";
    private final String SELECT_BY_ID = "SELECT * FROM friend INNER JOIN reader r ON friend.friend_reader = r.reader_id " +
            "WHERE friend_id = ?";
    private final String DELETE_BY_ID = "DELETE FROM friend WHERE friend_id = ?";
    private final String DELETE_ALL = "DELETE FROM friend";
    private final String UPDATE = "UPDATE friend SET friend_name = ?, friend_avatar = ?, friend_reader = ? WHERE " +
            "friend_id = ?";

    private final String SELECT_JOIN  = "SELECT * FROM friend " +
            "INNER JOIN book_friend ON friend.friend_id = book_friend.book_friend_friend_id " +
            "INNER JOIN book ON book.book_id = book_friend.book_friend_book_id " +
            "INNER JOIN reader r ON book.book_reader = r.reader_id ";

    private final String SELECT_JOIN_BY_ID = "SELECT * FROM friend " +
            "INNER JOIN book_friend ON friend.friend_id = book_friend.book_friend_friend_id " +
            "INNER JOIN book ON book.book_id = book_friend.book_friend_book_id " +
            "INNER JOIN reader r ON book.book_reader = r.reader_id " +
            "WHERE friend.friend_id = ?;";

    public FriendRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Friend> findAllFriends() {
        List<Friend> friends = this.jdbcTemplate.query(SELECT_ALL, new FriendMapper());
        Map<Long, List<Book>> booksMap = this.jdbcTemplate.query(SELECT_JOIN, new FriendMapExtractor());
        for (Friend friend : friends) {
            friend.setHistory(booksMap.get(friend.getId()));
        }
        return friends;
    }

    @Override
    public boolean createFriend(Friend friend) {
        int result = this.jdbcTemplate.update(INSERT, friend.getName(), friend.getAvatar(), friend.getReader().getId());
        return result != 0;
    }

    @Override
    public Friend findFriendById(Long id) {
        Friend friend = this.jdbcTemplate.queryForObject(SELECT_BY_ID, new Object [] {id}, new FriendMapper());
        Map<Long, List<Book>> booksMap = this.jdbcTemplate.query(SELECT_JOIN_BY_ID, new Object [] {id}, new FriendMapExtractor());
        friend.setHistory(booksMap.get(friend.getId()));
        return friend;
    }

    @Override
    public boolean deleteFriendById(Long id) {
        int result = this.jdbcTemplate.update(DELETE_BY_ID, id);
        return result != 0;
    }

    @Override
    public boolean deleteAllFriends() {
        int result = this.jdbcTemplate.update(DELETE_ALL);
        return result != 0;
    }

    @Override
    public boolean updateFriend(Long id, Friend friend) {
        int result = this.jdbcTemplate.update(UPDATE, friend.getName(), friend.getAvatar(), friend.getReader().getId(), id);
        return result != 0;
    }
}
