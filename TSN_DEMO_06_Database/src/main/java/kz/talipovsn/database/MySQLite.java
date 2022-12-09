package kz.talipovsn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MySQLite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION =5; // НОМЕР ВЕРСИИ БАЗЫ ДАННЫХ И ТАБЛИЦ !

    static final String DATABASE_NAME = "books"; // Имя базы данных

    static final String TABLE_NAME = "book_service"; // Имя таблицы
    static final String ID = "id"; // Поле с ID
    static final String NAME = "name"; // Поле с наименованием часов
    static final String NAME_LC = "name_lc"; // // Поле с наименованием часов в нижнем регистре
    static final String DESCRIPTION = "description"; // Поле с описанием
    static final String DESCRIPTION_LC = "description_lc"; // // Поле с описанием в нижнем регистре
    static final String PRICE = "price"; // Поле с ценой
    static final String RATINGP = "ratingP"; // Поле с рейтингом клиентов
    static final String RATINGCR = "ratingCr"; // Поле с рейтингом "экспертов"
    static final String LINK = "link"; // Поле с ссылкой

    static final String ASSETS_FILE_NAME = "clock.txt"; // Имя файла из ресурсов с данными для БД
    static final String DATA_SEPARATOR = "|"; // Разделитель данных в файле ресурсов с телефонами

    private Context context; // Контекст приложения



    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Метод создания базы данных и таблиц в ней
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT,"
                + NAME_LC + " TEXT,"
                + DESCRIPTION + " TEXT,"
                + DESCRIPTION_LC + " TEXT,"
                + PRICE + " INTEGER,"
                + RATINGP + " TEXT,"
                + RATINGCR + " TEXT,"
                + LINK + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        System.out.println(CREATE_CONTACTS_TABLE);
        loadDataFromAsset(context, ASSETS_FILE_NAME,  db);


    }

    // Метод при обновлении структуры базы данных и/или таблиц в ней
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        System.out.println("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Добавление нового контакта в БД
    public void addData(SQLiteDatabase db, String name, String description, int price, String ratingP, String ratingCr, String link) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(NAME_LC, name.toLowerCase());
        values.put(DESCRIPTION, description);
        values.put(DESCRIPTION_LC, description.toLowerCase());
        values.put(PRICE, price);
        values.put(RATINGP, ratingP);
        values.put(RATINGCR, ratingCr);
        values.put(LINK, link);
        db.insert(TABLE_NAME, null, values);
    }

    // Добавление записей в базу данных из файла ресурсов
    public void loadDataFromAsset(Context context, String fileName, SQLiteDatabase db) {
        BufferedReader in = null;

        try {
            // Открываем поток для работы с файлом с исходными данными
            InputStream is = context.getAssets().open(fileName);
            // Открываем буфер обмена для потока работы с файлом с исходными данными
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            while ((str = in.readLine()) != null) { // Читаем строку из файла
                String strTrim = str.trim(); // Убираем у строки пробелы с концов
                if (!strTrim.equals("")) { // Если строка не пустая, то
                    StringTokenizer st = new StringTokenizer(strTrim, DATA_SEPARATOR); // Нарезаем ее на части
                    String name = st.nextToken().trim();
                    String description = st.nextToken().trim();
                    String price = st.nextToken().trim();
                    String ratingP = st.nextToken().trim();
                    String ratingCr = st.nextToken().trim();
                    String link = st.nextToken().trim();
                    addData(db, name, description, Integer.parseInt(price), ratingP, ratingCr, link);
                }
            }

            // Обработчики ошибок
        } catch (IOException ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

    }

    // Получение значений данных из БД в виде строки с фильтром
    public String getData(String filter, Spinner spinner) {

        String selectQuery = "SELECT  * FROM " + TABLE_NAME; // Переменная для SQL-запроса

        long idSpin = spinner.getSelectedItemId();
        System.out.println(idSpin);
        if (filter.contains("'")){
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " LIMIT 0";
        }else if (idSpin == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + NAME_LC + " LIKE '%" + filter.toLowerCase() + "%'"
                    + " OR " + DESCRIPTION_LC + " LIKE '%" + filter.toLowerCase() + "%'"
                    + " OR " + RATINGP + " LIKE '%" + filter + "%'"
                    + " OR " + RATINGCR + " LIKE '%" + filter + "%'"
                    + " OR " + LINK + " LIKE '%" + filter + "%'" + ")";
        } else if (idSpin == 1) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + NAME_LC + " LIKE '%" + filter.toLowerCase() + "%'" + ")";
        } else if (idSpin == 2) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + DESCRIPTION_LC + " LIKE '%" + filter.toLowerCase() + "%'" + ")";
        } else if (idSpin == 3) {
            if (filter.isEmpty() | !filter.matches("[-+]?\\d+") ) {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " LIMIT 0"  ;
            } else {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                        + PRICE + " >= " + Integer.parseInt(filter);
            }

        } else if (idSpin == 4) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + RATINGP + " LIKE '%" + filter + "%'" + ")";
        } else if (idSpin == 5) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + RATINGCR + " LIKE '%" + filter + "%'" + ")";
        } else if (idSpin == 6) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + LINK + " LIKE '%" + filter + "%'" + ")";
        }

        SQLiteDatabase db = this.getReadableDatabase(); // Доступ к БД
        Cursor cursor = db.rawQuery(selectQuery, null); // Выполнение SQL-запроса

        StringBuilder data = new StringBuilder(); // Переменная для формирования данных из запроса


        int num = 0;
        if (cursor.moveToFirst()) { // Если есть хоть одна запись, то
            do { // Цикл по всем записям результата запроса
                int n = cursor.getColumnIndex(NAME);
                int d = cursor.getColumnIndex(DESCRIPTION);
                int p = cursor.getColumnIndex(PRICE);
                int rp = cursor.getColumnIndex(RATINGP);
                int rcr = cursor.getColumnIndex(RATINGCR);
                int l = cursor.getColumnIndex(LINK);
                String name = cursor.getString(n); // Чтение названия организации
                String description = cursor.getString(d);
                String price = cursor.getString(p);
                String ratingP = cursor.getString(rp);
                String ratingCr = cursor.getString(rcr);
                String link = cursor.getString(l); // Чтение телефонного номера
                data.append(String.valueOf(++num) + ") " + name + "\n "
                        + "Описание: " + description + "\n"
                        + "Цена (тг): " + price + "\n"
                        + "Рейтинг клиентов: " + ratingP + "\n"
                        + "Рейтинг экспертов: " + ratingCr + "\n"
                        + "Ссылка: " + link + "\n");
            } while (cursor.moveToNext()); // Цикл пока есть следующая запись
        }
        return data.toString(); // Возвращение результата
    }

}