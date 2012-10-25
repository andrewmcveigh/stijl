# stijl

A Clojure library designed to generate CSS from clojure vectors.

Inspiration taken from hiccup & sass.

## Syntax

### Selectors

Selectors are defined with a symbol, prefixed with a '$'. They can be anywhere
within the form, however it's customary to put them first.

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

## Usage

### in-style

    (in-style :margin [:top 0 :left auto]) => "margin-top: 0;margin-left: auto"

### css

    (css [$demo :margin-top px80]) => "demo {\nmargin-top: 80px;\n}\n"
    (css [$demo :margin-top px-8]) => "demo {\nmargin-top: -8px;\n}\n"

### style

    (style
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

    (style
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

### Mixins

    (defmixin border-radius
      ([tl tr bl br]
       (let [corners (mapv px [tl tr bl br])]
         `[:border-radius ~@corners
           :-moz-border-radius ~@corners
           :-webkit-border-radius ~@corners]))
      ([r]
       (border-radius r r r r)))

    (border-radius 5) => [:border-radius "5px" "5px" "5px" "5px"
                          :-moz-border-radius "5px" "5px" "5px" "5px"
                          :-webkit-border-radius "5px" "5px" "5px" "5px"]

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
