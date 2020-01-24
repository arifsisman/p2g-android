package vip.yazilim.p2g.android.util.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import vip.yazilim.p2g.android.data.p2g.User
import vip.yazilim.p2g.android.data.spotify.TokenModel

/**
 * @author mustafaarifsisman - 23.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val USER_TABLE_NAME = "User"
    private val TOKEN_TABLE_NAME = "Token"

    private val COL_ID = "id"
    private val COL_NAME = "name"
    private val COL_EMAIL = "email"
    private val COL_ROLE = "role"
    private val COL_ONLINE_STATUS = "online_status"
    private val COL_COUNTRY_CODE = "country_code"
    private val COL_IMAGE_URL = "image_url"
    private val COL_ANTHEM = "anthem"
    private val COL_SPOTIFY_PRODUCT_TYPE = "spotify_product_type"
    private val COL_SHOW_ACTIVITY_FLAG = "show_activity_flag"
    private val COL_SHOW_FRIENDS_FLAG = "show_friends_flag"
    private val COL_CREATION_DATE = "creation_date"

    private val COL_REFRESH_TOKEN = "refresh_token"
    private val COL_ACCESS_TOKEN = "access_token"
    private val COL_INSERT_DATE = "insert_timestamp"


    companion object {
        private val DATABASE_NAME = "SQLITE_DATABASE"
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = "CREATE TABLE $USER_TABLE_NAME (" +
                "$COL_ID TEXT PRIMARY KEY, " +
                "$COL_NAME  TEXT," +
                "$COL_EMAIL  TEXT," +
                "$COL_ROLE  TEXT," +
                "$COL_ONLINE_STATUS  TEXT," +
                "$COL_COUNTRY_CODE  TEXT," +
                "$COL_IMAGE_URL  TEXT," +
                "$COL_ANTHEM  TEXT," +
                "$COL_SPOTIFY_PRODUCT_TYPE  TEXT," +
                "$COL_SHOW_ACTIVITY_FLAG  BOOLEAN," +
                "$COL_SHOW_FRIENDS_FLAG  BOOLEAN," +
                "$COL_CREATION_DATE  TEXT" +
                ")"

        val createTokenTable = "CREATE TABLE $TOKEN_TABLE_NAME (" +
                "$COL_REFRESH_TOKEN TEXT PRIMARY KEY, " +
                "$COL_ACCESS_TOKEN  TEXT," +
                "$COL_INSERT_DATE  DATE default CURRENT_DATE" +
                ")"

        db?.execSQL(createUserTable)
        db?.execSQL(createTokenTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertData(user: User) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID, user.id)
        contentValues.put(COL_NAME, user.name)
        contentValues.put(COL_EMAIL, user.email)
        contentValues.put(COL_ROLE, user.role)
        contentValues.put(COL_ONLINE_STATUS, user.onlineStatus)
        contentValues.put(COL_COUNTRY_CODE, user.countryCode)
        contentValues.put(COL_IMAGE_URL, user.imageUrl)
        contentValues.put(COL_ANTHEM, user.anthem)
        contentValues.put(COL_SPOTIFY_PRODUCT_TYPE, user.spotifyProductType)
        contentValues.put(COL_SHOW_ACTIVITY_FLAG, user.showActivityFlag)
        contentValues.put(COL_SHOW_FRIENDS_FLAG, user.showFriendsFlag)
        contentValues.put(COL_CREATION_DATE, user.creationDate.format(formatter))

        sqliteDB.insert(USER_TABLE_NAME, null, contentValues)
    }

    fun readUser(): MutableList<User> {
        val userList = mutableListOf<User>()
        val sqliteDB = this.readableDatabase
        val query = "SELECT * FROM $USER_TABLE_NAME"
        val result = sqliteDB.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val userId = result.getString(result.getColumnIndex(COL_ID))
                val userName = result.getString(result.getColumnIndex(COL_NAME))
                val userEmail = result.getString(result.getColumnIndex(COL_EMAIL))
                val userRole = result.getString(result.getColumnIndex(COL_ROLE))
                val userOnlineStatus = result.getString(result.getColumnIndex(COL_ONLINE_STATUS))
                val userCountryCode = result.getString(result.getColumnIndex(COL_COUNTRY_CODE))
                val userImageUrl = result.getString(result.getColumnIndex(COL_IMAGE_URL))
                val userAnthem = result.getString(result.getColumnIndex(COL_ANTHEM))
                val userSpotifyProductType =
                    result.getString(result.getColumnIndex(COL_SPOTIFY_PRODUCT_TYPE))
                val userShowActivityFlag =
                    result.getInt(result.getColumnIndex(COL_SHOW_ACTIVITY_FLAG)) > 0
                val userShowFriendsFlag =
                    result.getInt(result.getColumnIndex(COL_SHOW_FRIENDS_FLAG)) > 0
                val userCreationDate =
                    LocalDateTime.parse(result.getString(result.getColumnIndex(COL_CREATION_DATE)))

                val user = User(
                    userId,
                    userName,
                    userEmail,
                    userRole,
                    userOnlineStatus,
                    userCountryCode,
                    userImageUrl,
                    userAnthem,
                    userSpotifyProductType,
                    userShowActivityFlag,
                    userShowFriendsFlag,
                    userCreationDate
                )
                userList.add(user)
            } while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return userList
    }

    fun insertData(tokenModel: TokenModel) {
//        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_REFRESH_TOKEN, tokenModel.refresh_token)
        contentValues.put(COL_ACCESS_TOKEN, tokenModel.refresh_token)
//        contentValues.put(COL_INSERT_DATE, LocalDateTime.now().toString())

        sqliteDB.insert(TOKEN_TABLE_NAME, null, contentValues)
    }

    fun readTokenModel(): MutableList<TokenModel> {
        val tokenModelList = mutableListOf<TokenModel>()
        val sqliteDB = this.readableDatabase
        val query = "SELECT * FROM $TOKEN_TABLE_NAME"
        val result = sqliteDB.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val refreshToken = result.getString(result.getColumnIndex(COL_REFRESH_TOKEN))
                val accessToken = result.getString(result.getColumnIndex(COL_ACCESS_TOKEN))

                val tokenModel = TokenModel(accessToken, refreshToken)

                tokenModelList.add(tokenModel)
            } while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return tokenModelList
    }

    fun deleteAllData() {
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(USER_TABLE_NAME, null, null)
        sqliteDB.delete(TOKEN_TABLE_NAME, null, null)
        sqliteDB.close()

    }
}