function ztb(){}
function Dtb(){}
function Htb(){}
function Qtb(){}
function Atb(a){this.a=a}
function Etb(a){this.a=a}
function Itb(a){this.a=a}
function Rtb(a,b){this.a=a;this.b=b}
function G4b(a,b){z4b(a,b);Il(a.Q,b)}
function Il(a,b){a.remove(b)}
function _Rb(){var a;if(!YRb||bSb()){a=new jxc;aSb(a);YRb=a}return YRb}
function bSb(){var a=$doc.cookie;if(a!=ZRb){ZRb=a;return true}else{return false}}
function cSb(a){a=encodeURIComponent(a);$doc.cookie=a+'=;expires=Fri, 02-Jan-1970 00:00:00 GMT'}
function utb(a,b){var c,d,e,f;Hl(a.c.Q);f=0;e=oC(_Rb());for(d=$tc(e);d.a.ee();){c=tP(fuc(d),1);D4b(a.c,c);mpc(c,b)&&(f=a.c.Q.options.length-1)}gk((ak(),_j),new Rtb(a,f))}
function vtb(a){var b,c,d,e;if(a.c.Q.options.length<1){q7b(a.a,AAc);q7b(a.b,AAc);return}d=a.c.Q.selectedIndex;b=C4b(a.c,d);c=(e=_Rb(),tP(e.Vd(b),1));q7b(a.a,b);q7b(a.b,c)}
function aSb(b){var c=$doc.cookie;if(c&&c!=AAc){var d=c.split('; ');for(var e=0;e<d.length;++e){var f,g;var i=d[e].indexOf(ABc);if(i==-1){f=d[e];g=AAc}else{f=d[e].substring(0,i);g=d[e].substring(i+1)}if($Rb){try{f=decodeURIComponent(f)}catch(a){}try{g=decodeURIComponent(g)}catch(a){}}b.Xd(f,g)}}}
function ttb(a){var b,c,d;c=new e2b(3,3);a.c=new I4b;b=new PVb('Supprimer');Be(b.Q,oJc,true);r1b(c,0,0,'<b><b>Cookies existants:<\/b><\/b>');u1b(c,0,1,a.c);u1b(c,0,2,b);a.a=new C7b;r1b(c,1,0,'<b><b>Nom:<\/b><\/b>');u1b(c,1,1,a.a);a.b=new C7b;d=new PVb('Sauvegarder Cookie');Be(d.Q,oJc,true);r1b(c,2,0,'<b><b>Valeur:<\/b><\/b>');u1b(c,2,1,a.b);u1b(c,2,2,d);Ie(d,new Atb(a),(oq(),oq(),nq));Ie(a.c,new Etb(a),(bq(),bq(),aq));Ie(b,new Itb(a),nq);utb(a,null);return c}
_=Atb.prototype=ztb.prototype=new Y;_.gC=function Btb(){return eX};_.oc=function Ctb(a){var b,c,d;c=tl(this.a.a.Q,VFc);d=tl(this.a.b.Q,VFc);b=new HO(C5(G5((new FO).p.getTime()),jAc));if(c.length<1){bTb('Vous devez indiquer un nom de cookie');return}dSb(c,d,b);utb(this.a,c)};_.cM={22:1,44:1};_.a=null;_=Etb.prototype=Dtb.prototype=new Y;_.gC=function Ftb(){return fX};_.nc=function Gtb(a){vtb(this.a)};_.cM={21:1,44:1};_.a=null;_=Itb.prototype=Htb.prototype=new Y;_.gC=function Jtb(){return gX};_.oc=function Ktb(a){var b,c;c=this.a.c.Q.selectedIndex;if(c>-1&&c<this.a.c.Q.options.length){b=C4b(this.a.c,c);cSb(b);G4b(this.a.c,c);vtb(this.a)}};_.cM={22:1,44:1};_.a=null;_=Ltb.prototype;_.ac=function Ptb(){d9(this.b,ttb(this.a))};_=Rtb.prototype=Qtb.prototype=new Y;_.cc=function Stb(){this.b<this.a.c.Q.options.length&&H4b(this.a.c,this.b);vtb(this.a)};_.gC=function Ttb(){return iX};_.a=null;_.b=0;var YRb=null,ZRb=null,$Rb=true;var eX=foc(tFc,'CwCookies$1'),fX=foc(tFc,'CwCookies$2'),gX=foc(tFc,'CwCookies$3'),iX=foc(tFc,'CwCookies$5');yAc(sj)(24);