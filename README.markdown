CWAC RichEditText: Letting Users Make Text Pretty
=================================================

Android's `EditText` widget supports formatted (a.k.a.,
"rich text") editing. It just lacks any way for the user
to supply formatting, and it does not provide much in the
way of convenience methods for a developer to, say, tie
in some sort of toolbar to allow users to format selections.

That's where `RichEditText` comes in.

`RichEditText` is a drop-in replacement for `EditText` that:

- Provides an action mode on Android 4.0+ that allows
users to format selected pieces of text
- Provides convenience methods to allow developers to 
trigger formatting for selected text via other means

This widget is packaged as an Android Studio library module, with
a `demo/` subdirectory containing a regular Android Studio app module
with a couple of activities demonstrating the use of
`RichEditText`.

This library also contains a series of utility classes for working
with rich text. In particular, it has code to convert a `Spanned` to
and from XHTML, as an alternative to the `toHtml()`
and `fromHtml()` methods on Android's `Html` class.

In addition to the documentation on this page,
partial JavaDocs are also available for [the editor widget](http://javadocs.commonsware.com/cwac/richedit/index.html)
and [the utility classes](http://javadocs.commonsware.com/cwac/richtextutils/index.html).

This Android library project is available as an artifact for use
with Gradle. To use that, add the following
blocks to your `build.gradle` file:

```groovy
repositories {
    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    compile 'com.commonsware.cwac:richedit:0.5.+'
}
```

Or, if you cannot use SSL, use `http://repo.commonsware.com` for the repository
URL.

If you are not using Gradle, download or clone this repo, and add the `richedit/`
module to your Android Studio project as a library module.

**NOTE**: This project is no longer compatible with Eclipse at the source
level, as of version 0.5.0. A ZIP file containing what should be an Eclipse-compatible
project is in [the releases area](https://github.com/commonsguy/cwac-richedit/releases).
However, this ZIP file has not been tested &mdash; please file bug reports
if you encounter problems with it.

**NOTE**: If you were using v0.2.0 with ActionBarSherlock, ActionBarSherlock
support was removed from this project as of v0.3.0. Please remain on v0.2.0,
or switch to the native API Level 11+ action bar (a.k.a., "15 is the new 10").

Usage: RichEditText
-----
Simply add `com.commonsware.cwac.richedit.RichEditText`
widgets to your layout as needed:

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.commonsware.cwac.richedit.RichEditText xmlns:android="http://schemas.android.com/apk/res/android"
  android:id="@+id/editor"
  android:layout_width="fill_parent"
  android:layout_height="fill_parent"
  android:gravity="top|left"
  android:inputType="textMultiLine">

  <requestFocus/>

</com.commonsware.cwac.richedit.RichEditText>
```

At this time, there are no custom attributes used by
`RichEditText`.

On its own, by default, `RichEditText` provides one means of users
applying formatting: the standard `<Ctrl>-<B>` for bold,
`<Ctrl>-<I>` for italics, and `<Ctrl>-<U>` for underline work if there
is a selection. You can disable this by calling
`setKeyboardShortcutsEnabled(false)`.

If you want an on-screen UI for formatting, you have two choices.

First, you can call
`enableActionModes()` on the `RichEditText`. This will add a "FORMAT"
entry on the action mode that comes up when the user highlights some
prose in the editor. Tapping that will allow the user to toggle various
effects. 

The action modes work so-so on phones at this time &mdash;
tablets work better. To get it to work on phones at all, you will need
to include `android:imeOptions="flagNoExtractUi"` as an attribute on the
`RichEditText`.

**NOTE**: The action modes do not work on Android 5.1. They will be
deprecated in an upcoming release of this library and will be removed
entirely before the library reaches 1.0. A stock toolbar implementation
will be provided instead as the out-of-the-box way to offer users the
ability to control effects.

Alternatively, you can have
your own toolbar or gesture interface or
whatever to allow users to format text. In that case, here are the two key
methods to call on `RichEditText`:

- `applyEffect()` changes the current selection, applying
or removing an effect (e.g., making the selection bold). The
first parameter is the effect to apply (e.g., `RichEditText.BOLD`).
The second parameter is the new value for the effect. Many
effects take boolean values, so `applyEffect(RichEditText.BOLD, true)`
would format the current selection as bold.

- `setOnSelectionChangedListener()` is where you register a
`RichEditText.OnSelectionChangedListener` object, which will
be called with `onSelectionChanged()` whenever the user changes
the selection in the widget (i.e., highlights text or taps
to un-select the highlight). You are provided the start and
end positions of the selection (as were supplied to `onSelectionChanged()`
to `RichEditText` itself by Android), plus a list of effects
that are active on that selection. This will allow you to 
update your toolbar to indicate what is and is not in use,
and so you know what to do when the user taps on one of
those toolbar buttons again.

### Supported Effects

At the time of this writing, here are the `RichEditText`
static data members for each supported effect:

- `BOLD`
- `ITALIC`
- `UNDERLINE`
- `STRIKETHROUGH`
- `SUPERSCRIPT`
- `SUBSCRIPT`
- `TYPEFACE`
- `ABSOLUTE_SIZE`
- `RELATIVE_SIZE`
- `URL`
- `BACKGROUND` (color)
- `FOREGROUND` (color)

There are other effects presently implemented, but they
will be revised shortly, including name and data type
changes, so don't mess with them yet.

To use the `BACKGROUND` and `FOREGROUND` effects, you will need to call
`setColorPicker()` on the `RichEditText` widget, supplying an implementation
of the `ColorPicker` interface. That has one required method: `pick()`.
It receives a `ColorPickerOperation`, on which you can call `hasColor()`
(returns `true` if we are editing an existing color) and `getColor()`
(returns the existing color value, if applicable). Your job is to 
collect a color from the user, then call either `onColorPicked()`
(supplying the color) or `onPickerDismissed()` (indicating that
the user abandoned the request for a color and that the selection
should remain unchanged). See the demo app for an example
implementation.

Known Limitations: RichEditText
-----------------
- This widget has not been tested with the AppCompat action bar backport.
Most likely, it will not work well. AppCompat support is planned, at
least to get a `Toolbar` implementation going. Tint support will be
added as well, if and only if the process for doing so is documented.

- The `demo` app uses a `ColorMixerActivity` from 
the [CWAC-ColorMixer library](https://github.com/commonsguy/cwac-colormixer)
for its implementation of `ColorPicker`. While easy to integrate, this approach
has one major flaw: the color picker remains in the foreground after
a configuration change. Since the demo activity is recreated, so its its
`RichEditText` widget, and any existing selection (or `ColorPickerOperation`)
is lost. What the demo app *should* do is dismiss the color picker on a
configuration change, since the chosen color will not be applied anyway.

Usage: XHTML Conversion
----------------
This library also contains classes to convert
`Spanned` objects to/from XHTML.

### Scope of Support

The primary objective of this conversion logic is to support the
formatting offered by the `RichEditText` widget.
Apps that wish to allow users to enter in rich text can use
`RichEditText`, then persist the `Spanned` using this library. Later
on, if the user wants to edit rich text entered previously, the app
can convert the XHTML back into a `Spanned` to supply to `RichEditText`.

A secondary objective is to allow the resulting persisted value to be
usable by anything that needs an XHTML representation of rich text.
For example, you might supply the XHTML to a Web service, or upload
it to a Web site. That's why XHTML is chosen as the representation format,
as opposed to some sort of `Serializable` or other binary packaging.

Whereas `Html.fromHtml()` is designed to parse semi-arbitrary HTML,
this library is not. You are welcome to feed it XHTML from wherever and
see if it works. As the saying goes, YMMV.

### Basic Parsing and Generating

Given a `Spanned` (e.g., `getText()` on a `RichEditText`), to get an
XHTML representation of the `Spanned`, create an instance of
`SpannedXhtmlGenerator` and call `toXhtml()` on it. This will return
a `String` of XHTML.

Later on, to get the `Spanned` back from that XHTML, create an instance
of a `SpannableStringGenerator` and call `fromXhtml()` on it, passing
it the `String` of XHTML, and getting back a a `Spannable` that you can
use with `RichEditText` or whatever.

And that's pretty much it.

### Conversion Rules

A stock set of rules, embodied in a collection of `SpanTagHandler`
instances, are applied to convert the `Spanned` to XHTML and back again:

| `CharacterStyle`      | XHTML Tag Structure                     |
| --------------------- | --------------------------------------- |
| `AbsoluteSizeSpan`    | `<span style="font-size:...px;">`      |
| `BackgroundColorSpan` | `<span style="background-color:#...">` |
| `BulletSpan`          | `<li>` inside of a `<ul>` |
| `ForegroundColorSpan` | `<font color="...">`                    |
| `LineAlignmentSpan`   | `<div style="text-align:...">` or `<ul style="text-align:...">` |
| `RelativeSizeSpan`    | `<span style="font-size:...%;">`       |
| `StrikethroughSpan`   | `<strike>`                              |
| `StyleSpan`           | `<b>` or `<i>`                          |
| `SubscriptSpan`       | `<sub>`                                 |
| `SuperscriptSpan`     | `<sup>`                                 |
| `TypefaceSpan`        | `<span style="font-family:...;">`       |
| `UnderlineSpan`       | `<u>`                                   |
| `URLSpan`             | `<a href="...">`                        |

### Customizing the Conversion

If there are new `CharacterStyle` subclasses that you want to support,
and you want to do so on a global (process-level) basis, create
a subclass of `SpanTagHandler` and register it via
`registerGlobalSpanTagHandler()` on the `SpanTagHandler` class.

If you want to override the default rules, create a subclass
(or subclasses) of `SpanTagHandler` for those rules. Then, create
an instance of `SpanTagRoster` and register your handlers via
`registerSpanTagHandler()` on the roster. You can pass in your
roster to the constructor of `SpannedXhtmlGenerator` or
`SpannableStringGenerator`.

There are a bunch of implemenations of `SpanTagHandler`, for the
stock rules, in the `com.commonsware.cwac.richtextutils.handler`
package, so you can see what creating these looks like.
There is also a `ClassSpanTagHandler` that you can use to
use a `<span class="...">` tag for a particular `CharacterStyle`, if
you want to use CSS classes for the actual formatting rules.

Note, though, that if you customize the rules by any of these
mechanisms, it is incumbent upon you to *keep* those customizations.
If you generate XHTML using one set of rules, you need to use
the same (or a compatible) set of rules to restore the `Spanned`.

Known Limitations
-----------------
- Two start tags in sequence may be flipped in order during conversion.
So, for example, suppose you had `<b><i>Foo</i></b>`, and you converted
that into a `Spanned`, then back into XHTML. The resulting XHTML could
be the same or could be `<i><b>Foo</b></i>`.

- It is possible that multiple `<span>` elements will be applied for the
same text (e.g., it is adjusted using a `RelativeSizeSpan` and a
`BackgroundColorSpan`). No attempt is made to coalesce those `<span>`
elements into one, even though from an XHTML standpoint, this is certainly
possible (and perhaps even desired).

- The XHTML generated by this library is unofficial until the library
reaches 1.0. At that point, the XHTML specification will remain fixed
through point-level releases (e.g., 1.1) until the next major release
(e.g., 2.0). Hence, until the library reaches 1.0, and for major
releases after that, you may need to go through some cleanup logic, as
your XHTML may not be parsed the same way as it had been in earlier
versions of the library.

Dependencies
------------
There are no third-party dependencies at this time for the library.
The demo app depends not only on this library but also on
the [CWAC-ColorMixer library](https://github.com/commonsguy/cwac-colormixer).

This project should work on API Level 11 and higher, except for any portions that
may be noted otherwise in this document. Please report bugs if you find features
that do not work on API Level 11 and are not noted as requiring a higher version.

Version
-------
This is version v0.5.1 of this module, meaning it is creeping towards
respectability.

Demo
----
In the `demo/` module you will find
a sample activity that demonstrates the use of `RichEditText`.
In the `demo-utils/` module you will find a sample activity that demonstrates
the use of the the XHTML utilties.

Also, the `androidTest/` directory in the `main/` sourceset of the
`richedit/` module contains a number of instrumentation tests.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please post a question
on [StackOverflow](http://stackoverflow.com/questions/ask) tagged with `commonsware-cwac`
and `android` after [searching to see if there already is an answer](https://stackoverflow.com/search?q=[android]+richedittext).
Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, or if you have a feature request,
please post an [issue](https://github.com/commonsguy/cwac-richedit/issues).
Be certain to include complete steps for reproducing the issue.

Do not ask for help via Twitter.

Also, if you plan on hacking
on the code with an eye for contributing something back,
please open an issue that we can use for discussing
implementation details. Just lobbing a pull request over
the fence may work, but it may not.

Release Notes
-------------
- v0.5.1: fixed [issue 15](https://github.com/commonsguy/cwac-richedit/issues/15) and [issue 17](https://github.com/commonsguy/cwac-richedit/issues/17)
- v0.5.0: added preliminary support for bullets, added XHTML conversion classes
- v0.4.0: added support for size, color, and URL effects
- v0.3.1: updated for Android Studio 1.0 and new AAR publishing system
- v0.3.0: removed ActionBarSherlock support, icon for FORMAT action mode item, fixed clipboard bug, added Gradle support
- v0.2.0: added keyboard shortcuts for bold/italic/underline and test suite, bug fixes
- v0.1.1: added `disableActionModes()` and fixed bug related to conditional action mode usage
- v0.1: added action mode support using ActionBarSherlock for pre-Honeycomb devices
- v0.0.3: removed `RichEditor`, replaced it with custom action modes
- v0.0.2: added `RichEditor` and made various fixes
- v0.0.1: initial release

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

