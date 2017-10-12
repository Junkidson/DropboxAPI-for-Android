package com.dropbox.core.examples.dbapp2_3;

/**
 * Created by keisuke on 2017/05/10.
 */

import android.util.Log;

import org.apache.tools.ant.Main;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*import static com.dropbox.android.sample.DBRoulette.cal;
import static com.dropbox.android.sample.DBRoulette.sdf;*/

public class Zip_Create2 {
    /**
     * 指定されたディレクトリ内のファイルを ZIP アーカイブし、指定されたパスに作成します。
     * デフォルト文字コードは Shift_JIS ですので、日本語ファイル名も対応できます。
     *
     * @paramfullPath 圧縮後の出力ファイル名をフルパスで指定 ( 例: C:/sample.zip )
     * @paramdirectory 圧縮するディレクトリ ( 例; C:/sample )
     * @return 処理結果 true:圧縮成功 false:圧縮失敗
     */
    public static boolean compressDirectory(String filePath, String directory ) {

        Log.v("LifeCycle", "1Zip作成開始");
        File baseFile = new File(filePath);											//作成するZipファイル名
        File file = new File(directory);											//Zip化するディレクトリ
        ZipOutputStream outZip = null;
        try {
            // ZIPファイル出力オブジェクト作成
            outZip = new ZipOutputStream(new FileOutputStream(baseFile));
            archive(outZip, baseFile, file);
        } catch ( Exception e ) {
            // ZIP圧縮失敗
            return false;
        } finally {
            // ZIPエントリクローズ
            if ( outZip != null ) {
                try { outZip.closeEntry(); } catch (Exception e) {}
                try { outZip.flush(); } catch (Exception e) {}
                try { outZip.close(); } catch (Exception e) {}
            }
        }
        Log.v("LifeCycle", "1Zip作成完了");
        try
        {
            //ディレクトリ削除(Post_HS)
            Log.v("LifeCycle", "ディレクトリ(Post_HS)削除開始");
            File Post_HS = new File(MainActivity.Directory);
            File[] delPost_HS = Post_HS.listFiles();
            for(int i = 0; i<delPost_HS.length; i++)
            {
                delPost_HS[i].delete();
            }
            Log.v("LifeCycle", "ディレクトリ「Postbox_to_HS」削除完了");
        }
        catch(Exception e)
        {
            System.out.println("ディレクトリ削除失敗");
            System.out.print(e);
/*
            String caln = sdf.format(cal.getTime());
*/

        }

        return true;
    }

    /**
     * ディレクトリ圧縮のための再帰処理
     *
     * @param outZip ZipOutputStream
     * @param baseFile File 保存先ファイル
     * @param file File 圧縮したいファイル
     */
    private static void archive(ZipOutputStream outZip, File baseFile, File targetFile) {
        if ( targetFile.isDirectory() ) {
            File[] files = targetFile.listFiles();
            for (File f : files) {
                if ( f.isDirectory() ) {
                    archive(outZip, baseFile, f);
                } else {
                    if ( !f.getAbsoluteFile().equals(baseFile)  ) {
                        // 圧縮処理
                        archive(outZip, baseFile, f, f.getAbsolutePath().replace(baseFile.getParent(), "").substring(1), "Shift_JIS");
                    }
                }
            }
        }
    }

    /**
     * 圧縮処理
     *
     * @param outZip ZipOutputStream
     * @param baseFile File 保存先ファイル
     * @param targetFile File 圧縮したいファイル
     * @parma entryName 保存ファイル名
     * @param enc 文字コード
     */
    private static boolean archive(ZipOutputStream outZip, File baseFile, File targetFile, String entryName, String enc) {
        // 圧縮レベル設定
        outZip.setLevel(5);

        // 文字コードを指定
        outZip.setEncoding(enc);
        try {

            // ZIPエントリ作成
            outZip.putNextEntry(new ZipEntry(entryName));

            // 圧縮ファイル読み込みストリーム取得
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(targetFile));

            // 圧縮ファイルをZIPファイルに出力
            int readSize = 0;
            byte buffer[] = new byte[1024]; // 読み込みバッファ
            while ((readSize = in.read(buffer, 0, buffer.length)) != -1) {
                outZip.write(buffer, 0, readSize);
            }
            // クローズ処理
            in.close();
            // ZIPエントリクローズ
            outZip.closeEntry();
        } catch ( Exception e ) {
            // ZIP圧縮失敗
            return false;
        }
        return true;
    }

}
