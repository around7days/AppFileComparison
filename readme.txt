■glob構文
*.java			.java で終わるファイル名を表すパスに一致します
*.*				ドットを含むファイル名に一致します
*.{java,class}	.java または .class で終わるファイル名に一致します
foo.?			foo. と 1 文字の拡張子で始まるファイル名に一致します
/home/*/*		UNIX プラットフォームでの /home/gus/data に一致します
/home/**		UNIX プラットフォームでの /home/gus や /home/gus/data に一致します
C:\\*			Windows プラットフォームでの C:\foo や C:\bar に一致します (バックスラッシュがエスケープされている。Java 言語のリテラル文字列としてのパターンは "C:\\\\*" になる)


■参考例
### 比較対象(glob構文)
includeName=css/*;WEB-INF/**;*.*
　※cssフォルダ直下を対象
　※WEB-INF配下全てを対象
　※ルート直下の拡張子付きを対象

### 除外対象ファイル(glob構文)
excludeName=.*;**/.*;**/CVS/**
　※.で始まるファイルを除外(.cvs .classpath等)
　※CVSフォルダ配下の除外


■参考URL
https://docs.oracle.com/javase/jp/7/api/java/nio/file/FileSystem.html