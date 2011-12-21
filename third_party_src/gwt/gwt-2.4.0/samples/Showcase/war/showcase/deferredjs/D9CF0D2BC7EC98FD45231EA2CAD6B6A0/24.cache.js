function Jtb(){}
function Ntb(){}
function Rtb(){}
function $tb(){}
function Ktb(a){this.b=a}
function Otb(a){this.b=a}
function Stb(a){this.b=a}
function _tb(a,b){this.b=a;this.c=b}
function J4b(a,b){C4b(a,b);Zl(a.R,b)}
function Zl(a,b){a.remove(b)}
function YRb(){var a;if(!VRb||$Rb()){a=new nxc;ZRb(a);VRb=a}return VRb}
function $Rb(){var a=$doc.cookie;if(a!=WRb){WRb=a;return true}else{return false}}
function _Rb(a){a=encodeURIComponent(a);$doc.cookie=a+'=;expires=Fri, 02-Jan-1970 00:00:00 GMT'}
function Etb(a,b){var c,d,e,f;Yl(a.d.R);f=0;e=CC(YRb());for(d=cuc(e);d.b.ie();){c=HP(juc(d),1);G4b(a.d,c);qpc(c,b)&&(f=a.d.R.options.length-1)}vk((pk(),ok),new _tb(a,f))}
function Ftb(a){var b,c,d,e;if(a.d.R.options.length<1){t7b(a.b,EAc);t7b(a.c,EAc);return}d=a.d.R.selectedIndex;b=F4b(a.d,d);c=(e=YRb(),HP(e.Zd(b),1));t7b(a.b,b);t7b(a.c,c)}
function ZRb(b){var c=$doc.cookie;if(c&&c!=EAc){var d=c.split('; ');for(var e=0;e<d.length;++e){var f,g;var i=d[e].indexOf(EBc);if(i==-1){f=d[e];g=EAc}else{f=d[e].substring(0,i);g=d[e].substring(i+1)}if(XRb){try{f=decodeURIComponent(f)}catch(a){}try{g=decodeURIComponent(g)}catch(a){}}b._d(f,g)}}}
function Dtb(a){var b,c,d;c=new h2b(3,3);a.d=new L4b;b=new SVb('Supprimer');Pe(b.R,zJc,true);u1b(c,0,0,'<b><b>Cookies existants:<\/b><\/b>');x1b(c,0,1,a.d);x1b(c,0,2,b);a.b=new F7b;u1b(c,1,0,'<b><b>Nom:<\/b><\/b>');x1b(c,1,1,a.b);a.c=new F7b;d=new SVb('Sauvegarder Cookie');Pe(d.R,zJc,true);u1b(c,2,0,'<b><b>Valeur:<\/b><\/b>');x1b(c,2,1,a.c);x1b(c,2,2,d);We(d,new Ktb(a),(Cq(),Cq(),Bq));We(a.d,new Otb(a),(pq(),pq(),oq));We(b,new Stb(a),Bq);Etb(a,null);return c}
_=Ktb.prototype=Jtb.prototype=new Y;_.gC=function Ltb(){return vX};_.sc=function Mtb(a){var b,c,d;c=Kl(this.b.b.R,fGc);d=Kl(this.b.c.R,fGc);b=new VO(S5(W5((new TO).q.getTime()),nAc));if(c.length<1){$Sb('Vous devez indiquer un nom de cookie');return}aSb(c,d,b);Etb(this.b,c)};_.cM={22:1,44:1};_.b=null;_=Otb.prototype=Ntb.prototype=new Y;_.gC=function Ptb(){return wX};_.rc=function Qtb(a){Ftb(this.b)};_.cM={21:1,44:1};_.b=null;_=Stb.prototype=Rtb.prototype=new Y;_.gC=function Ttb(){return xX};_.sc=function Utb(a){var b,c;c=this.b.d.R.selectedIndex;if(c>-1&&c<this.b.d.R.options.length){b=F4b(this.b.d,c);_Rb(b);J4b(this.b.d,c);Ftb(this.b)}};_.cM={22:1,44:1};_.b=null;_=Vtb.prototype;_.fc=function Ztb(){n9(this.c,Dtb(this.b))};_=_tb.prototype=$tb.prototype=new Y;_.hc=function aub(){this.c<this.b.d.R.options.length&&K4b(this.b.d,this.c);Ftb(this.b)};_.gC=function bub(){return zX};_.b=null;_.c=0;var VRb=null,WRb=null,XRb=true;var vX=joc(GFc,'CwCookies$1'),wX=joc(GFc,'CwCookies$2'),xX=joc(GFc,'CwCookies$3'),zX=joc(GFc,'CwCookies$5');CAc(Hj)(24);