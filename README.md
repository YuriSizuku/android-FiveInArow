# FiveInArow_Xrelated
This is a five_in_a_row project with ai [kuon](https://github.com/YuriSizuku/FiveInArow) on android.

### Eviroment:
* JDK: jdk1.8.0_25
* NDK: android-ndk-r10d
* Android Studio 1.3.2
* Min&Target Sdk Version: API 15: Android 4.0.3 (IceCreamSandwich)
* Compile Sdk Version: API 23: Android 6.0 (Marshmallow)
* Testing environment: s4_i9508_4.4.2, NOX_v2.3.0.0_4.4.2

### Structures:
>JAVA(com.devseed.fiveinarow):  
>>activity  
>>>MainActivity.java  
>>>NetWorkActivity.java  
>>>ConfigActivity.java  
>>>DbViewActivity.java  
>>>ChessGameActivity.java  
>>>AboutActivity.java  
>  
>>adapter  
>>>ChessStepAdapter.java  
>>>Config2Adapter.java  
>  
>>data  
>>>AppValues.java     //preference  
>>>ChessIO.java     //IO save,load  
>  
>>dialog  
>>>ColorDialog.java  
>>>FileDialogView.java  
>  
>>fragment  
>>>ConfigFragment.java  
>>>Config1Fragment.java  
>>>Config2Fragment.java  
>>>Config3Fragment.java  
>>>SteplogFragment.java     //chess stack view  
>  
>>view  
>>>ChessBoardView.java     //draw board and chesses  
>  
>>ChessKernel.java     //chess kernel functions  
>  
>JNI  
>>//head files  
>>Ai_kuon.h  
>>Ai_hitagi.h  
>>JniFunc.h  
>>stdFunc.h  
>>stdValue.h  
>>//auto generate  
>>com_devseed_fiveinarow_ChessKernel.h  
>>com_devseed_fiveinarow_ChessKernel_ChessStep.h  
>>com_devseed_fiveinarow_ChessKernel_StepNode.h  
>>//ai functions  
>>Ai__kuon.cpp     //game trees, alpha-beta pruning tree  
>>Ai_hitagi.cpp     //reinformentce learning, ann,bp-td  
>>JniFunc.cpp     //jni interfaces  
>>stdFunc.cpp     //standrad data structures  
>  
>EXTERNLIB  
>>externAndroidLibrary     //file manager lib  
>>externAndroidFileExplorer     //file manager interface  

### Fuctions:
* see steplog in the left 
* view the step on the chess
* save and load the chessboard(change the path)
* rollback or back to a certain step
* change the colors and the chessboard bg
* remember the config you set
* change the language(jp,en,cn)

### Coming soon.....
* more powerful ai(クオン+ with ANN)
* play with others in network
* save the result by sqlite,view the statistics
* scaling the chessboard
* define the chessboard size

### ScreenShots:
![](https://github.com/YuriSizuku/FiveInArow_Xrelated/blob/master/bin/p1.png)
![](https://github.com/YuriSizuku/FiveInArow_Xrelated/blob/master/bin/p2.png)
![](https://github.com/YuriSizuku/FiveInArow_Xrelated/blob/master/bin/p3.png)
![](https://github.com/YuriSizuku/FiveInArow_Xrelated/blob/master/bin/p4.png)

### About:
<p>I used about 1 month(2015.10.26~2015.12.4) to develop this android application,
<p>about 2 weeks to create ai [クオン(with alpha-beta gametree)](https://github.com/YuriSizuku/FiveInArow),
<p>the other time is to learn android and create the chessboard interface
<p>inluding jni,android data access(pull xml,SharedPreference),views,activity filer,baseadapter,android graphies etc.
<p>You can get the source code from https://github.com/YuriSizuku/FiveInArow_Xrelated
<p>You can contract me via devseed@163.com.



