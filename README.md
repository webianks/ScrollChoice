[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ScrollChoice-red.svg?style=flat)](https://android-arsenal.com/details/1/5398)
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=102)](https://opensource.org/licenses/MIT)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/webianks/ScrollChoice/blob/master/LICENSE)

# ScrollChoice
Scrollable view which can be used to give different choices to user with nice ui.

# Preview
<img src="https://github.com/webianks/ScrollChoice/blob/master/screens/screen_one.png" align="left" height="700" width="400" >
<img src="https://github.com/webianks/ScrollChoice/blob/master/screens/screen_two.png" height="700" width="400" >

# Add With Gradle Dependency
```groovy
compile 'com.webianks.library:scroll-choice:1.0.1'
```
**Maven:**
```xml
<dependency>
  <groupId>com.webianks.library</groupId>
  <artifactId>scroll-choice</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```
# Min SDK
15

# Compile SDK
25

# Add ScrollChoice to layout
```xml
<com.webianks.library.scroll_choice.ScrollChoice
        android:id="@+id/scroll_choice"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

# From Java

**Get the reference of ScrollChoice**
```java
ScrollChoice scrollChoice = (ScrollChoice) findViewById(R.id.scroll_choice);
```
**Prepare your list items**

```java
List<String> data = new ArrayList<>();
data.add("Brazil");
data.add("USA");
data.add("China");
data.add("Pakistan");
data.add("Australia");
data.add("India");
data.add("Nepal");
data.add("Sri Lanka");
data.add("Spain");
data.add("Italy");
data.add("France");
```

**Add to the ScrollChoice object with default selected item index**
```java
scrollChoice.addItems(data,5);
```

**Attach listener to listen to know which item was selected**
```java
scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                Log.d("webi",name);
            }
        });
```

Here 5 is the index of default selected item.

# Customization Through XML
```xml
<!--Change background Color-->
    app:scroll_background_color="#212121"
<!--Change selected item's background -->
    app:scroll_selected_item_background="#000000"
<!--Change text color-->
    app:scroll_item_text_color="#f5f5f5"
<!--Show/Hide atmospheric gradient effect on top-bottom items-->
    app:scroll_atmospheric="true"
<!--Hide/show scroll indicator-->
    app:scroll_indicator="true"
<!--Change indicator color-->
    app:scroll_indicator_color="#000000"
<!--Align all items-->
    app:scroll_item_align="center"
<!--Set selected item's text color-->
    app:scroll_selected_item_text_color="?attr/colorPrimary"
<!--Set selected item position-->
    app:scroll_selected_item_position="center"
<!--Set different text size-->
    app:scroll_item_text_size="17sp"
<!--Set different indicator size-->
    app:scroll_indicator_size="1dp"      
```

# Test APK

<a href="https://github.com/webianks/ScrollChoice/blob/master/test_apk/app-debug.apk"><img src="https://github.com/webianks/HatkeMessenger/blob/master/screens/download.png" height="74" width="50"></a>



**Based on the awesome WheelPicker taken from <a href="https://github.com/florent37/SingleDateAndTimePicker" target="_blank" >SingleDateAndTimePicker</a> by <a href="https://github.com/florent37" target="_blank">Florent CHAMPIGNY</a>.**

## License

```
MIT License

Copyright (c) 2017 Ramankit Singh

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
