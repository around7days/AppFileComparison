■■ファイル同期化バッチ■■
Linuxのrsyncに似た機能を実装したバッチ
　※rsyncと同様にファイルのハッシュチェックが可能


■glob構文例
*.java			.java で終わるファイル名を表すパスに一致します
*.*				ドットを含むファイル名に一致します
*.{java,class}	.java または .class で終わるファイル名に一致します
foo.?			foo. と 1 文字の拡張子で始まるファイル名に一致します
/home/*/*		UNIX プラットフォームでの /home/gus/data に一致します
/home/**		UNIX プラットフォームでの /home/gus や /home/gus/data に一致します
WEB-INF/**		UNIX プラットフォームでの WEB-INF配下全てを対象とします
C:\\*			Windows プラットフォームでの C:\foo や C:\bar に一致します (バックスラッシュがエスケープされている。)

https://docs.oracle.com/javase/jp/7/api/java/nio/file/FileSystem.html
