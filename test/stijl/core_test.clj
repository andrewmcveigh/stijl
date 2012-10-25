(ns stijl.core-test
  (:use clojure.test
        stijl.core
        stijl.mixin))

(deftest css-test
  (testing
    "FIXME, I fail."
    (with-redefs [*whitespace* true]
      (is (= (css
               [$body $#panel $.bootstrap $div.container>li+a
                :padding 0 auto 0 auto
                :margin [:left "auto"
                         :right "auto"]
                [$div#container
                 [$div.well
                  :top "1px"
                  :margin [:bottom "0"
                           :top "10px"]
                  [$a.btn $button.btn $a.btn-primary
                   :border-radius "5px"]]]
                [$span
                 :display "block"]]))
"body, #panel, .bootstrap, div.container > li + a {
padding: 0 auto 0 auto;
margin-left: auto;
margin-right: auto
}

body div#container div.well, #panel div#container div.well, .bootstrap div#container div.well, div.container > li + a div#container div.well {
top: 1px;
margin-bottom: 0;
margin-top: 10px
}
body div#container div.well a.btn, body div#container div.well button.btn, body div#container div.well a.btn-primary, #panel div#container div.well a.btn, #panel div#container div.well button.btn, #panel div#container div.well a.btn-primary, .bootstrap div#container div.well a.btn, .bootstrap div#container div.well button.btn, .bootstrap div#container div.well a.btn-primary, div.container > li + a div#container div.well a.btn, div.container > li + a div#container div.well button.btn, div.container > li + a div#container div.well a.btn-primary {
border-radius: 5px
}
body span, #panel span, .bootstrap span, div.container > li + a span {
display: block
}
"))
    (is (= (css [($input :type "text") "$a[href~=www]" :margin auto])
           "input[type=text], a[href~=www] {\nmargin: auto;\n}\n"))
    (is (= (css [$body :margin 0 [$.inlay (border-radius 5) :margin 0]]
                [$div.class (border-radius 10)]))
"body {
margin: 0
}
body .inlay {
border-radius: 5px 5px 5px 5px;
-moz-border-radius: 5px 5px 5px 5px;
-webkit-border-radius: 5px 5px 5px 5px;
margin: 0
}
div.class {
border-radius: 10px 10px 10px 10px;
-moz-border-radius: 10px 10px 10px 10px;
-webkit-border-radius: 10px 10px 10px 10px
}
")))

(deftest in-style-test
  (is (= (in-style :margin [:top 0 :left auto])
         "margin-top: 0;margin-left: auto;")))

(css [$demo :margin-top px80])
(css [$demo :margin-top px-8])

(css-map [$ttt (border-radius 4)
          :ttt (rgba 1 2 3 5)])
(style
    [$body :background-color (rgba 42 153 255 0.17)]
    [$.well
     :background-color (rgba 0 0 0 0.05)
     :overflow hidden
     :padding [:bottom 0]])

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

(css
  [$table
   [$tr :line-height "18px"
    [$td $th :padding 0
     [$a.btn :color "#AAA"]
     [$span $p :color "#339"]]
    [$th :font-weight bold]]])

(deftest css-big-test
  (is (= (css
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
                         :image (stijl.mixin/url "/img/linen-grad.png")
                         :repeat repeat-x]
            :box-shadow "0 1px 3px rgba(0, 0, 0, 0.50), 0 -1px 0 rgba(0, 0, 0, 0.3) inset"
            :min-height (px 40)]
           [$.navbar
            [$.brand
             :color "#FC0"
             :font [:family Arial]
             :text-shadow 0 (px -1) 0 (rgba 0 0 0 0.77) \, 0 (px 1) 0 (rgba 255 255 255 0.2)
             [$em
              :color "#444"
              :font [:weight bold]
              :text-shadow 0 px1 0 (rgba 255 255 255 0.57)]]
            [$.nav>li>a
             :color (rgba 0 0 0 0.65)
             :font [:family Arial
                    :size (px 16)]
             :text-shadow 0 px-1 0 (rgba 0 0 0 0.20) \, 0 px1 0 (rgba 255 255 255 0.40)]
            [$.nav>li>a:hover
             :color "#eee"
             :text-shadow 0 px-1 0 (rgba 0 0 0 0.57)]
            [$.nav
             [$.active>a $.active>a:hover
              :background-color (rgba 41 59 77 0.29)
              :border [:left solid px1 (rgba 0 0 0 0.2)
                       :right solid px1 (rgba 0 0 0 0.2)]
              :color "#FFF"
              :text-shadow 0 "-1px" 0 (rgba 0 0 0 0.57)]
             [$li.dropdown.active>.dropdown-toggle
              $li.dropdown.open.active>.dropdown-toggle
              :background-color (rgba 41 59 77 0.29)]]
            [$.divider-vertical
             :background-color (rgba 0 0 0 0.33)
             :border-right px1 solid (rgba 255 255 255 0.56)
             :height px40
             :margin 0 px9
             :overflow hidden
             :width px1]]
           [$div.top :margin [:top px80]]
           [$table.table
            (box-shadow 0 px1 px3 (rgba 0 0 0 0.5) \, inset 0 px-1 0 (rgba 0 0 0 0.1))
            :background-color "#F0EFEB"
            :width px939]
           )
         "body {\nbackground-color: rgba(42, 153, 255, 0.17);\n}\n.well {\nbackground-color: rgba(0, 0, 0, 0.05);\noverflow: hidden;\npadding-bottom: 0;\n}\ninput, textarea, select, .uneditable-input {\nborder: 1px solid rgba(0, 0, 0, 0.25);\nbackground-color: rgba(255, 255, 255, 0.9);\n}\n\n.input-prepend .add-on, .input-append .add-on {\nborder: 1px solid rgba(0, 0, 0, 0.25);\n}\n.btn {\nborder-color: rgba(0, 0, 0, 0.5);\n}\n\n.form-horizontal .form-actions {\npadding-left: 180px;\n}\n.form-actions {\nbackground-color: rgba(0, 0, 0, 0.05);\nborder-top: solid;\nborder-1px: rgba(0, 0, 0, 0.2);\nmargin: 18px -20px -20px;\n}\nlegend {\nborder-bottom: solid;\nborder-1px: rgba(0, 0, 0, 0.2);\n}\n.navbar-inner {\nbackground-color: #9AB;\nbackground-image: url(/img/linen-grad.png);\nbackground-repeat: repeat-x;\nbox-shadow: 0 1px 3px rgba(0, 0, 0, 0.50), 0 -1px 0 rgba(0, 0, 0, 0.3) inset;\nmin-height: 40px;\n}\n\n.navbar .brand {\ncolor: #FC0;\nfont-family: Arial;\ntext-shadow: 0 -1px 0 rgba(0, 0, 0, 0.77) , 0 1px 0 rgba(255, 255, 255, 0.2);\n}\n.navbar .brand em {\ncolor: #444;\nfont-weight: bold;\ntext-shadow: 0 1px 0 rgba(255, 255, 255, 0.57);\n}\n.navbar .nav > li > a {\ncolor: rgba(0, 0, 0, 0.65);\nfont-family: Arial;\nfont-size: 16px;\ntext-shadow: 0 -1px 0 rgba(0, 0, 0, 0.2) , 0 1px 0 rgba(255, 255, 255, 0.4);\n}\n.navbar .nav > li > a:hover {\ncolor: #eee;\ntext-shadow: 0 -1px 0 rgba(0, 0, 0, 0.57);\n}\n\n.navbar .nav .active > a, .navbar .nav .active > a:hover {\nbackground-color: rgba(41, 59, 77, 0.29);\nborder-left: solid;\nborder-1px: rgba(0, 0, 0, 0.2);\nborder-right: solid;\nborder-1px: rgba(0, 0, 0, 0.2);\ncolor: #FFF;\ntext-shadow: 0 -1px 0 rgba(0, 0, 0, 0.57);\n}\n.navbar .nav li.dropdown.active > .dropdown-toggle, .navbar .nav li.dropdown.open.active > .dropdown-toggle {\nbackground-color: rgba(41, 59, 77, 0.29);\n}\n.navbar .divider-vertical {\nbackground-color: rgba(0, 0, 0, 0.33);\nborder-right: 1px solid rgba(255, 255, 255, 0.56);\nheight: 40px;\nmargin: 0 9px;\noverflow: hidden;\nwidth: 1px;\n}\ndiv.top {\nmargin-top: 80px;\n}\ntable.table {\nbox-shadow: 0 1px 3px rgba(0, 0, 0, 0.5) , inset 0 -1px 0 rgba(0, 0, 0, 0.1);\n-moz-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5) , inset 0 -1px 0 rgba(0, 0, 0, 0.1);\n-webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5) , inset 0 -1px 0 rgba(0, 0, 0, 0.1);\nbackground-color: #F0EFEB;\nwidth: 939px;\n}\n")))
