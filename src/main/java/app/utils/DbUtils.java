package app.utils;

public class DbUtils {

    public static void close(AutoCloseable closeable){
        if(closeable == null){
            return;
        }

        try{
            closeable.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
