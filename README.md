# SIC Assembler
## 使用方式
使用cmd執行java檔 並輸入要讀入的文字檔檔名，若未輸入檔名則無法開始執行。

![](https://i.imgur.com/zNuzdyh.png)

成功執行後會將結果顯示出來，並輸出List與ObjectCode的文字檔。

![](https://i.imgur.com/P40eaE4.png)

## 可處理的格式輸入
程式碼中的標籤、指令、運算元之間需用空白或Tab隔開。

如需標示註解，則註解開頭需加上”/∗”，且結尾處須加上”∗/”
```asm
    /*start from 1000*/
    COPY  START	1000
    /*這行是註解*/
```

## 輸出
當判斷SIC程式碼正確無誤時，會將結果顯示出來，並輸出List與ObjectCode的文字檔。

若有錯誤，則會顯示錯誤訊息。
* 有發生label名稱重複時會結束程式，並顯示錯誤訊息，提醒使用者哪一個label名稱重複。

## 可處理的addressing modes
可以處理基本的SIC指令模式。

## 可以的assembler directives
* START：指定程式名稱和起始位址，若無指定則以0000作為起始位址。
* END：指示原始程式的結束處，並指定程式中第一個可執行的指令。
* BYTE：可判斷 C 與 X來定義是字元或十六進位的常數。WORD：定義一個字組的整數常數，可用負數表示。
* RESB：保留所示數量的位元組。
* RESW：保留所示數量的字組。
