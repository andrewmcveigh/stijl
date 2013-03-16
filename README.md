# stijl

Stijl is the Dutch word for "Style". It's a library designed for generating CSS
in Clojure.

Inspiration for Stijl has been taken from
[Hiccup](https://github.com/weavejester/hiccup) &
[Sass](http://sass-lang.com/).

## Installation

Stijl is now [in Clojars](https://clojars.org/com.dirtybrussels/stijl). Put the
following dependency in your project.clj.

    [com.dirtybrussels/stijl "0.1.0-SNAPSHOT"]

## Syntax

### Selectors

Selectors are defined with a symbol, prefixed with a <code>$</code>. They can
be anywhere within the form, however it's customary to put them first.

#### Simple Selectors

    $body
    $div.container
    $input#email

#### Child Selectors

    $body>p

#### Sibling Selectors

    $h1+h2

#### Decendant Selectors

    $div*p

#### Attribute Selectors

Clojure's syntax doesn't allow for <code>[]</code> in symbols, therefore we
must specify attributes differently.

    $input:type=text

equates to:

    input[type=text]

For more complex attributes, such as <code>href</code>, or where we want to use
a different "equality" operator we can fall back to using a string selector.

    "$a[rel~=\"copyright\"]"
    "$a[href=\"http://www.w3.org/\"]"

equates to:

    a[rel~="copyright"]
    a[href="http://www.w3.org/"]

### Rules

A Stijl rule is analogous to a CSS rule.

    [$h1 :font-family sans-serif]

is the same as:

    [$h1 :font [:family sans-serif]]

equates to:

    h1 { font-family: sans-serif }

### Grouping

Rules can be grouped.

    [$h1 $h2 $h3 :font [:family sans-serif]]

equates to:

    h1, h2, h3 { font-family: sans-serif }

### Descendants

Descendant selectors can be defined with nested rules:

    [$#container
     [$span :color "#333"]]

equates to:

    #container span { color: #333 }

Nesting can get quite complex:

    [$table
     [$tr :line-height "18px"
       [$td $th :padding 0
         [$a.btn :color "#AAA"]
         [$span $p :color "#339"]]
       [$th :font-weight bold]]]

equates to:

    table tr { line-height: 18px; }
    table tr td, table tr th { padding: 0; }
    table tr td a.btn, table tr th a.btn { color: #AAA; }
    table tr td span, table tr td p, table tr th span, table tr th p {
      color: #339;
    }
    table tr th { font-weight: bold; }

## Shortcuts

Shortcuts are simple Clojure functions that return a string. They allow us to
write CSS properties in a more "Clojure-like" way. We can also use them in
"compositions" or "mixins".

    (defn px [n]
      (str n "px"))

    user> (px 3)

    => "3px"
    

    (defn rgba [r g b a]
      (format "rgba(%s, %s, %s, %s)" r g b a))

    user> (rgba 0 0 0 0.75)

    => "rgba(0, 0, 0, 0.75)"
    

    (defn url [x]
      (format "url(%s)" x))

    user> (url "/img/button.png")

    => "url(/img/button.png)"

### Symbol Shortcuts

The are also a set of <code>symbol</code> shortcuts. As Clojure syntax doesn't
allow us to write <code>1px</code> without quoting as a <code>string</code>, we
offer a symbol shortcut: <code>px1</code>.

Symbol shortcuts currently work with measurement units:

    ["em" "ex" "px" "ch" "in" "cm" "mm" "pt" "pc" "%"]
    
    em-10 => "-10em"
    px100 => "100px"
    in6   => "6in"
    %100  => "100%"

## Mixins

Mixins allow us to pre-define CSS properties to use in rules. They are composed
of Clojure functions that return a vector of properties.

The defmixin <code>macro</code> decorates the function <code>var</code> with
<code>{:stijl/mixin true}</code> metadata, so Stijl knows it's a mixin.

    (defmixin border-radius
      ([tl tr bl br]
       (let [corners (mapv px [tl tr bl br])]
         `[:border-radius ~@corners
           :-moz-border-radius ~@corners
           :-webkit-border-radius ~@corners]))
      ([r]
       (border-radius r r r r)))


    user> (border-radius 5)

     => [:border-radius "5px" "5px" "5px" "5px"
         :-moz-border-radius "5px" "5px" "5px" "5px"
         :-webkit-border-radius "5px" "5px" "5px" "5px"]


    user> (css [$div.drop-down (border-radius 3) :margin 0])

    => div.dropdown {
         border-radius: 5px 5px 5px 5px;
         -moz-border-radius: 5px 5px 5px 5px;
         -webkit-border-radius: 5px 5px 5px 5px;
         margin: 0;
       }

Mixins can be used anywhere in the form, but the will always be rendered at
the top of the form, in the order they were written.

## Usage

There are three macros in Stijl.

### in-style

Generates raw CSS properties. Can be used in "hiccup" templates
<code>:style</code> attributes.

    user> (in-style :margin [:top 0 :left auto])

    => "margin-top: 0;margin-left: auto"

E.G.,

    [:a {:href "http://github.com" :style (in-style :margin [:bottom px10 :top 0])} "linky"]

Will, render to:

    [:a {:href "http://github.com :style "margin-bottom: 10px; margin-top: 0"} "linky"]

And after "hiccup" renders it:

    <a href="http://github.com style="margin-bottom: 10px; margin-top: 0">
      linky
    </a>

### css

Generates raw CSS. Can be output to a file, or used dynamically.

    (css [$demo :margin-top px80]) => "demo {\nmargin-top: 80px;\n}\n"
    (css [$demo :margin-top px-8]) => "demo {\nmargin-top: -8px;\n}\n"

### style

Wraps <code>css</code> output in a hiccup-style <code>style</code> block.

    user> (style
            [$body :background-color (rgba 42 153 255 0.17)]
            [$.well
             :background-color (rgba 0 0 0 0.05)
             :overflow hidden
             :padding [:bottom 0]])

    => [:style {:type "text/css"}
        "body {
        background-color: rgba(42, 153, 255, 0.17);
        }
        .well {
        background-color: rgba(0, 0, 0, 0.05);
        overflow: hidden;
        padding-bottom: 0;
        }
        "]

    user> (style
            [$body :background-color (rgba 42 153 255 0.17)]
            [$.well
             :background-color (rgba 0 0 0 0.05)
             :overflow hidden
             :padding [:bottom 0]]
            [$input $textarea $select $.uneditable-input
             :border (px 1) solid (rgba 0 0 0 0.25)
             :background-color (rgba 255 255 255 0.9)]
            [$.input-prepend $.input-append
             [$.add-on :border (px 1) solid (rgba 0 0 0 0.25)]]
            [$.btn :border [:color (rgba 0 0 0 0.5)]]
            [$.form-horizontal
             [$.form-actions :padding [:left (px 180)]]]
            [$.form-actions
             :background-color (rgba 0 0 0 0.05)
             :border [:top solid (px 1) (rgba 0 0 0 0.2)]
             :margin "18px -20px -20px"]
            [$legend :border [:bottom solid (px 1) (rgba 0 0 0 0.2)]]
            [$.navbar-inner
             :background [:color "#9AB"
                          :image (url "/img/linen-grad.png")
                          :repeat repeat-x]
             :box-shadow "0 1px 3px rgba(0, 0, 0, 0.50), 0 -1px 0 rgba(0, 0, 0, 0.3) inset"
             :min-height (px 40)]
            [$.navbar
             [$.brand
              :font [:family Arial]
              :text-shadow 0 (px -1) 0 (rgba 0 0 0 0.77) \, 0 (px 1) 0 (rgba 255 255 255 0.2)]])

    => [:style {:type "text/css"}
       "body {
        background-color: rgba(42, 153, 255, 0.17);
        }
        .well {
        background-color: rgba(0, 0, 0, 0.05);
        overflow: hidden;
        padding-bottom: 0;
        }
        input, textarea, select, .uneditable-input {
        border: 1px solid rgba(0, 0, 0, 0.25);
        background-color: rgba(255, 255, 255, 0.9);
        }
        
        .input-prepend .add-on, .input-append .add-on {
        border: 1px solid rgba(0, 0, 0, 0.25);
        }
        .btn {
        border-color: rgba(0, 0, 0, 0.5);
        }
        
        .form-horizontal .form-actions {
        padding-left: 180px;
        }
        .form-actions {
        background-color: rgba(0, 0, 0, 0.05);
        border-top: solid;
        border-1px: rgba(0, 0, 0, 0.2);
        margin: 18px -20px -20px;
        }
        legend {
        border-bottom: solid;
        border-1px: rgba(0, 0, 0, 0.2);
        }
        .navbar-inner {
        background-color: #9AB;
        background-image: url(/img/linen-grad.png);
        background-repeat: repeat-x;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.50), 0 -1px 0 rgba(0, 0, 0, 0.3) inset;
        min-height: 40px;
        }
        
        .navbar .brand {
        font-family: Arial;
        text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.77) , 0 1px 0 rgba(255, 255, 255, 0.2);
        }
        "]

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
