# APMSample

Android App性能监控方案示例，只展示核心原理。里面包含现有的成熟的开源组件 + 部分自研组件。

现有的成熟开源组件基本上已经在个人博客上进行了讲解，此项目旨在进行统一的归纳梳理。

此示例的详细解释可看博客：[APM示例说明](https://blog.yorek.xyz/android/library/apm-sample/)

---

Java代码插桩采用[YorekLiu/MethodTracer](https://github.com/YorekLiu/MethodTracer)，开箱即用。

### 编译说明

1. AndResGuard插件需要适配AGP 7.x，我这里fork了一个:[YorekLiu/AndResGuard](https://github.com/YorekLiu/AndResGuard)，需要自行publish到mavenLocal
2. 7z工具需要自行替换到适合自己机器的，因为com.tencent.mm:SevenZip现在已经不能拉取到了，且也没有适配m1芯片，可以到[https://www.7-zip.org/download.html](https://www.7-zip.org/download.html)进行下载后，替换7zz即可
