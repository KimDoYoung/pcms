/**
 * @license Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
CKEDITOR.plugins.addExternal('fmath_formula', 'plugins/fmath_formula/', 'plugin.js');
//CKEDITOR.plugins.addExternal('geogebra','plugins/geogebra/','plugin.js');
//CKEDITOR.plugins.addExternal('svgedit','plugins/svgedit/','plugin.js');

CKEDITOR.editorConfig = function( config ) {
    config.extraPlugins = 'fmath_formula'; //수식편집기
    config.allowedContent = true;
    config.enterMode = CKEDITOR.ENTER_BR;
    config.autoParagraph = false;
    //
    config.fillEmptyBlocks = false;
    config.forceEnterMode = false;
    config.ignoreEmptyParagraph = true;


    config.language = 'ko';


    config.toolbar_Normal = [
        {name: 'document', items: ['Source']},
        {name: 'basicstyles', items: ['Bold', 'Italic',  'Underline', 'Font', 'FontSize', 'TextColor', 'BGColor']},
        {name: 'align', items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']},
        {name: 'paragraph', items: ['NumberedList', 'BulletedList']},
        {name: 'insert', items: ['Image', 'SpecialChar', 'Smiley']}
       // { name: 'insert', items: ['Image','SpecialChar','Smiley','-','fmath_formula']}
    ];
    config.toolbar_Fomula = [
        {name: 'document', items: ['Source']},
        {name: 'basicstyles', items: ['Bold', 'Italic', 'Underline', 'Font', 'FontSize', 'TextColor', 'BGColor']},
        {name: 'align', items: ['JustifyLeft', 'JustifyCenter', 'JustifyBlock']},
        //{name: 'insert', items: ['Image', 'SpecialChar', 'Smiley']}
        { name: 'insert', items: ['Image','SpecialChar','Smiley','-','fmath_formula']}
    ];
    config.toolbar_Simple = [
        {name: 'document', items: ['Source']},
        {name: 'basicstyles', items: ['Bold', 'Italic', 'Underline']},
        //{name: 'insert', items: ['Image', 'SpecialChar', 'Smiley']}
        { name: 'insert', items: ['Image','SpecialChar','-','fmath_formula']}
    ];
    config.toolbar = "Normal";

    config.specialChars = [
        "&euro;", "&lsquo;", "&rsquo;", "&ldquo;", "&rdquo;", "&ndash;", "&mdash;", "&iexcl;", "&cent;", "&pound;", "&curren;", "&yen;", "&brvbar;", "&sect;", "&uml;",
        "&copy;", "&ordf;", "&laquo;", "&not;", "&reg;", "&macr;", "&deg;", "&sup2;", "&sup3;", "&acute;", "&micro;", "&para;", "&middot;", "&cedil;", "&sup1;", "&ordm;",
        "&raquo;", "&frac14;", "&frac12;", "&frac34;", "&iquest;", "&Agrave;", "&Aacute;", "&Acirc;", "&Atilde;", "&Auml;", "&Aring;", "&AElig;", "&Ccedil;", "&Egrave;",
        "&Eacute;", "&Ecirc;", "&Euml;", "&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;", "&ETH;", "&Ntilde;", "&Ograve;", "&Oacute;", "&Ocirc;", "&Otilde;", "&Ouml;", "&times;",
        "&Oslash;", "&Ugrave;", "&Uacute;", "&Ucirc;", "&Uuml;", "&Yacute;", "&THORN;", "&szlig;", "&agrave;", "&aacute;", "&acirc;", "&atilde;", "&auml;", "&aring;", "&aelig;",
        "&ccedil;", "&egrave;", "&eacute;", "&ecirc;", "&euml;", "&igrave;", "&iacute;", "&icirc;", "&iuml;", "&eth;", "&ntilde;", "&ograve;", "&oacute;", "&ocirc;", "&otilde;",
        "&ouml;", "&divide;", "&oslash;", "&ugrave;", "&uacute;", "&ucirc;", "&uuml;", "&yacute;", "&thorn;", "&yuml;", "&OElig;", "&oelig;", "&#372;", "&#374", "&#373", "&#375;",
        "&sbquo;", "&#8219;", "&bdquo;", "&hellip;", "&trade;", "&#9658;", "&bull;", "&rarr;", "&rArr;", "&hArr;", "&diams;", "&asymp;",
        "㉠", "㉡", "㉢", "㉣", "㉤", "㉥", "㉦", "㉧", "㉨", "㉩", "㉪", "㉫", "㉬", "㉭",
        "㉮", "㉯", "㉰", "㉱", "㉲", "㉳", "㉴", "㉵", "㉶", "㉷", "㉸", "㉹", "㉺", "㉻",
        "㈀", "㈁", "㈂", "㈃", "㈄", "㈅", "㈆", "㈇", "㈈", "㈉", "㈊", "㈋", "㈌","㈍",
        "㈎", "㈏", "㈐", "㈑", "㈒", "㈓", "㈔", "㈕", "㈖", "㈗", "㈘", "㈙", "㈚", "㈛",
        "ⓐ", "ⓑ", "ⓒ", "ⓓ", "ⓔ", "ⓕ", "ⓖ", "ⓗ", "ⓘ", "ⓙ", "ⓚ", "ⓛ", "ⓜ", "ⓝ", "ⓞ", "ⓞ", "ⓟ", "ⓠ", "ⓡ", "ⓢ", "ⓣ", "ⓤ", "ⓥ", "ⓦ", "ⓧ", "ⓨ", "ⓩ",
        "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮",
        "⒜", "⒝", "⒞", "⒟", "⒠", "⒡", "⒢", "⒣", "⒤", "⒥", "⒦", "⒧", "⒨", "⒩", "⒪", "⒫", "⒬", "⒭", "⒮", "⒯", "⒰", "⒱", "⒲", "⒳", "⒴", "⒵",
        "⑴", "⑵", "⑶", "⑷", "⑸", "⑹", "⑺", "⑻", "⑼", "⑽", "⑾", "⑿", "⒀", "⒁", "⒂",
        "＄", "％", "￦", "Ｆ", "′", "″", "℃", "A", "￠", "￡", "￥", "¤", "℉", "‰", "㎕", "㎖", "㎗", "ℓ", "㎘", "㏄", "㎣", "㎤",
        "㎥", "㎥", "㎦", "㎙", "㎚", "㎛", "㎜", "㎝", "㎞", "㎟", "㎠", "㎡", "㎢", "㏊", "㎍", "㎎", "㎏", "㏏",
        "㎈", "㎉", "㏈", "㎧", "㎨", "㎰", "㎱", "㎲", "㎳", "㎴", "㎵", "㎶", "㎷", "㎸", "㎹", "㎀", "㎁", "㎂",
        "㎃", "㎺", "㎻", "㎼", "㎽", "㎾", "㎿", "㎐", "㎑", "㎒", "㎓", "㎔", "Ω", "㏀", "㏁", "㎊", "㎋", "㎌",
        "㏖", "㏅", "㎭", "㎮", "㎯", "㏛", "㎩", "㎪", "㎫", "㎬", "㏝", "㏐", "㏃", "㏉", "㏜", "㏆",
        "＋", "－", "＜", "＝", "＞", "±", "×", "÷", "≠", "≤", "≥", "∞", "∴", "♂", "♀", "∠", "⊥", "⌒", "∂", "∇", "≡", "≒", "≪", "≫", "√", "∽", "∝", "∵",
        "∫", "∬", "∈", "∋", "⊆", "⊇", "⊂", "⊃", "∪", "∩", "∧", "∨", "￢", "⇒", "⇔", "∀", "∃", "∮", "∑", "∏",
        "＃", "＆", "＊", "＠", "§", "※", "☆", "★", "○", "●", "◎", "◇", "◆", "□", "■", "△", "▲", "▽", "▼", "→", "←", "↑", "↓",
        "↔", "〓", "◁", "◀", "▷", "▶", "♤", "♠", "♡", "♥", "♧", "♣", "⊙", "◈", "▣", "◐", "◑", "▒", "▤", "▥", "▨", "▧", "▦",
        "▩", "♨", "☏", "☎", "☜", "☞", "¶", "†", "‡", "↕", "↗", "↙", "↖", "↘", "♩", "♪", "♬", "㉿", "㈜", "№", "㏇", "™", "㏂", "㏘",
        "℡", "?", "ª", "º",
        "Α","Β","Γ","Δ","Ε","Ζ","Η","Θ","Ι","Κ","Λ","Μ","Ν","Ξ","Ο","Π","Ρ","Σ","Τ","Υ","Φ","Χ","Ψ","Ω","α","β","γ","δ","ε","ζ","η","θ","ι","κ","λ","μ","ν","ξ","ο","π","ρ","σ","τ","υ","φ","χ","ψ","ω"
    ];
    //config.mathJaxLib = '\/js\/mathjax\/MathJax.js?config=Accessible-full';
};
