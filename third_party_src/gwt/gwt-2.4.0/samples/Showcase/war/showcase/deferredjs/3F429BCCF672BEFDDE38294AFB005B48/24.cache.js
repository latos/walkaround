function Xtb(){}
function _tb(){}
function dub(){}
function mub(){}
function aub(a){this.b=a}
function eub(a){this.b=a}
function Ytb(a){this.b=a}
function nub(a,b){this.b=a;this.c=b}
function W4b(a,b){P4b(a,b);pm(a.R,b)}
function pm(a,b){a.remove(b)}
function hSb(){var a;if(!eSb||jSb()){a=new Hxc;iSb(a);eSb=a}return eSb}
function jSb(){var a=$doc.cookie;if(a!=fSb){fSb=a;return true}else{return false}}
function kSb(a){a=encodeURIComponent(a);$doc.cookie=a+'=;expires=Fri, 02-Jan-1970 00:00:00 GMT'}
function Stb(a,b){var c,d,e,f;om(a.d.R);f=0;e=LC(hSb());for(d=wuc(e);d.b.me();){c=QP(Duc(d),1);T4b(a.d,c);Kpc(c,b)&&(f=a.d.R.options.length-1)}vk((pk(),ok),new nub(a,f))}
function Ttb(a){var b,c,d,e;if(a.d.R.options.length<1){H7b(a.b,YAc);H7b(a.c,YAc);return}d=a.d.R.selectedIndex;b=S4b(a.d,d);c=(e=hSb(),QP(e.be(b),1));H7b(a.b,b);H7b(a.c,c)}
function iSb(b){var c=$doc.cookie;if(c&&c!=YAc){var d=c.split('; ');for(var e=0;e<d.length;++e){var f,g;var i=d[e].indexOf(eCc);if(i==-1){f=d[e];g=YAc}else{f=d[e].substring(0,i);g=d[e].substring(i+1)}if(gSb){try{f=decodeURIComponent(f)}catch(a){}try{g=decodeURIComponent(g)}catch(a){}}b.de(f,g)}}}
function Rtb(a){var b,c,d;c=new u2b(3,3);a.d=new Y4b;b=new bWb('Supprimer');Qe(b.R,QJc,true);F1b(c,0,0,'<b><b>Cookies existants:<\/b><\/b>');I1b(c,0,1,a.d);I1b(c,0,2,b);a.b=new T7b;F1b(c,1,0,'<b><b>Nom:<\/b><\/b>');I1b(c,1,1,a.b);a.c=new T7b;d=new bWb('Sauvegarder Cookie');Qe(d.R,QJc,true);F1b(c,2,0,'<b><b>Valeur:<\/b><\/b>');I1b(c,2,1,a.c);I1b(c,2,2,d);Xe(d,new Ytb(a),(Lq(),Lq(),Kq));Xe(a.d,new aub(a),(yq(),yq(),xq));Xe(b,new eub(a),Kq);Stb(a,null);return c}
_=Ytb.prototype=Xtb.prototype=new Y;_.gC=function Ztb(){return HX};_.wc=function $tb(a){var b,c,d;c=am(this.b.b.R,xGc);d=am(this.b.c.R,xGc);b=new cP(e6(i6((new aP).q.getTime()),HAc));if(c.length<1){kTb('Vous devez indiquer un nom de cookie');return}lSb(c,d,b);Stb(this.b,c)};_.cM={22:1,44:1};_.b=null;_=aub.prototype=_tb.prototype=new Y;_.gC=function bub(){return IX};_.vc=function cub(a){Ttb(this.b)};_.cM={21:1,44:1};_.b=null;_=eub.prototype=dub.prototype=new Y;_.gC=function fub(){return JX};_.wc=function gub(a){var b,c;c=this.b.d.R.selectedIndex;if(c>-1&&c<this.b.d.R.options.length){b=S4b(this.b.d,c);kSb(b);W4b(this.b.d,c);Ttb(this.b)}};_.cM={22:1,44:1};_.b=null;_=hub.prototype;_.fc=function lub(){B9(this.c,Rtb(this.b))};_=nub.prototype=mub.prototype=new Y;_.hc=function oub(){this.c<this.b.d.R.options.length&&X4b(this.b.d,this.c);Ttb(this.b)};_.gC=function pub(){return LX};_.b=null;_.c=0;var eSb=null,fSb=null,gSb=true;var HX=Doc(YFc,'CwCookies$1'),IX=Doc(YFc,'CwCookies$2'),JX=Doc(YFc,'CwCookies$3'),LX=Doc(YFc,'CwCookies$5');WAc(Hj)(24);