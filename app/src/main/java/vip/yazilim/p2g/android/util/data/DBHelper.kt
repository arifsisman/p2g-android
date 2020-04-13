package vip.yazilim.p2g.android.util.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.spotify.TokenModel

/**
 * @author mustafaarifsisman - 23.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Deprecated("Not required")
class DBHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME, null,
        DATABASE_VERSION
    ) {
    private val USER_TABLE_NAME = "User"
    private val TOKEN_TABLE_NAME = "Token"
    private val VOTE_TABLE_NAME = "Vote"

    private val COL_ID = "table_id"
    private val COL_USER_ID = "id"
    private val COL_USER_NAME = "name"
    private val COL_USER_EMAIL = "email"
    private val COL_USER_ROLE = "role"
    private val COL_USER_ONLINE_STATUS = "online_status"
    private val COL_USER_COUNTRY_CODE = "country_code"
    private val COL_USER_IMAGE_URL = "image_url"
    private val COL_USER_ANTHEM = "anthem"
    private val COL_USER_SPOTIFY_PRODUCT_TYPE = "spotify_product_type"
    private val COL_USER_SHOW_ACTIVITY_FLAG = "show_activity_flag"
    private val COL_USER_SHOW_FRIENDS_FLAG = "show_friends_flag"
    private val COL_USER_CREATION_DATE = "creation_date"
    private val COL_USER_LAST_LOGIN = "last_login"

    private val COL_REFRESH_TOKEN = "refresh_token"
    private val COL_ACCESS_TOKEN = "access_token"
    private val COL_INSERT_DATE = "insert_timestamp"

    private val COL_ROOM_ID = "room_id"
    private val COL_SONG_ID = "song_id"

    companion object {
        private val DATABASE_NAME = "SQLITE_DATABASE"
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
//        val createUserTable = "CREATE TABLE $USER_TABLE_NAME (" +
//                "$COL_ID INTEGER PRIMARY KEY, " +
//                "$COL_USER_ID TEXT, " +
//                "$COL_USER_NAME  TEXT," +
//                "$COL_USER_EMAIL  TEXT," +
//                "$COL_USER_ROLE  TEXT," +
//                "$COL_USER_ONLINE_STATUS  TEXT," +
//                "$COL_USER_COUNTRY_CODE  TEXT," +
//                "$COL_USER_IMAGE_URL  TEXT," +
//                "$COL_USER_ANTHEM  TEXT," +
//                "$COL_USER_SPOTIFY_PRODUCT_TYPE  TEXT," +
//                "$COL_USER_SHOW_ACTIVITY_FLAG  BOOLEAN," +
//                "$COL_USER_SHOW_FRIENDS_FLAG  BOOLEAN," +
//                "$COL_USER_CREATION_DATE  TEXT" +
//                ")"
//
//        val createTokenTable = "CREATE TABLE $TOKEN_TABLE_NAME (" +
//                "$COL_ID INTEGER PRIMARY KEY, " +
//                "$COL_REFRESH_TOKEN TEXT, " +
//                "$COL_ACCESS_TOKEN  TEXT," +
//                "$COL_INSERT_DATE  DATE default CURRENT_DATE" +
//                ")"
//
//        db?.execSQL(createUserTable)
//        db?.execSQL(createTokenTable)

        val createSongVoteTable = "CREATE TABLE $VOTE_TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY," +
                "$COL_ROOM_ID TEXT," +
                "$COL_SONG_ID TEXT)"

        db?.execSQL(createSongVoteTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertData(user: User) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_USER_ID, user.id)
        contentValues.put(COL_USER_NAME, user.name)
        contentValues.put(COL_USER_EMAIL, user.email)
        contentValues.put(COL_USER_ROLE, user.role)
        contentValues.put(COL_USER_ONLINE_STATUS, user.onlineStatus)
        contentValues.put(COL_USER_COUNTRY_CODE, user.countryCode)
        contentValues.put(COL_USER_IMAGE_URL, user.imageUrl)
        contentValues.put(COL_USER_ANTHEM, user.anthemSongId)
        contentValues.put(COL_USER_CREATION_DATE, user.creationDate.format(formatter))
        contentValues.put(COL_USER_LAST_LOGIN, user.lastLogin.format(formatter))

        sqliteDB.insert(USER_TABLE_NAME, null, contentValues)
    }

    fun insertVotedSong(room: Room, song: Song) {
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ROOM_ID, room.id)
        contentValues.put(COL_SONG_ID, song.id)
        sqliteDB.insert(VOTE_TABLE_NAME, null, contentValues)
    }

    fun isVotedBefore(room: Room, song: Song): Boolean {
        val sqliteDB = readableDatabase
        val query =
            "SELECT * FROM $VOTE_TABLE_NAME WHERE ${COL_SONG_ID}=${song.id} AND ${COL_ROOM_ID}=${room.id}"
        val result = sqliteDB.rawQuery(query, null)

        val count = result.count
        result.close()
        sqliteDB.close()

        return count > 0
    }

    fun readUser(): User {
        val userList = mutableListOf<User>()
        val sqliteDB = this.readableDatabase
        val query = "SELECT * FROM $USER_TABLE_NAME"
        val result = sqliteDB.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val userId = result.getString(result.getColumnIndex(COL_USER_ID))
                val userName = result.getString(result.getColumnIndex(COL_USER_NAME))
                val userEmail = result.getString(result.getColumnIndex(COL_USER_EMAIL))
                val userRole = result.getString(result.getColumnIndex(COL_USER_ROLE))
                val userOnlineStatus =
                    result.getString(result.getColumnIndex(COL_USER_ONLINE_STATUS))
                val userCountryCode = result.getString(result.getColumnIndex(COL_USER_COUNTRY_CODE))
                val userImageUrl = result.getString(result.getColumnIndex(COL_USER_IMAGE_URL))
                val userAnthem = result.getString(result.getColumnIndex(COL_USER_ANTHEM))
                val userCreationDate =
                    LocalDateTime.parse(
                        result.getString(result.getColumnIndex(COL_USER_CREATION_DATE))
                    )
                val lastLogin = LocalDateTime.parse(
                    result.getString(result.getColumnIndex(COL_USER_LAST_LOGIN))
                )

                val user = User(
                    userId,
                    userName,
                    userEmail,
                    userRole,
                    userOnlineStatus,
                    userCountryCode,
                    userImageUrl,
                    userAnthem,
                    userCreationDate,
                    lastLogin
                )
                userList.add(user)
            } while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return userList.last()
    }

    fun insertData(tokenModel: TokenModel) {
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_REFRESH_TOKEN, tokenModel.refresh_token)
        contentValues.put(COL_ACCESS_TOKEN, tokenModel.refresh_token)

        sqliteDB.insert(TOKEN_TABLE_NAME, null, contentValues)
    }

//    fun readTokenModel(): TokenModel {
//        val tokenModelList = mutableListOf<TokenModel>()
//        val sqliteDB = this.readableDatabase
//        val query = "SELECT * FROM $TOKEN_TABLE_NAME"
//        val result = sqliteDB.rawQuery(query, null)
//        if (result.moveToFirst()) {
//            do {
//                val refreshToken = result.getString(result.getColumnIndex(COL_REFRESH_TOKEN))
//                val accessToken = result.getString(result.getColumnIndex(COL_ACCESS_TOKEN))
//
//                val tokenModel = TokenModel(accessToken, refreshToken)
//
//                tokenModelList.add(tokenModel)
//            } while (result.moveToNext())
//        }
//        result.close()
//        sqliteDB.close()
//        return tokenModelList.last()
//    }

    fun deleteAllData() {
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(USER_TABLE_NAME, null, null)
        sqliteDB.delete(TOKEN_TABLE_NAME, null, null)
        sqliteDB.close()

    }

    fun isUserExists(): Boolean {
        val sqliteDB = this.readableDatabase
        val query = "SELECT * FROM $USER_TABLE_NAME"
        val result = sqliteDB.rawQuery(query, null)

        val userCount = result.count
        result.close()
        sqliteDB.close()

        return userCount > 0
    }
}