@Dao
public interface ContactDao {
    @Insert
    void insert(Contact contact);

    @Query("SELECT * FROM contacts WHERE user_id = :userId")
    List<Contact> getContactsForUser(long userId);
    // other queries...
}
