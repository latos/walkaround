function Avb(){}
function Evb(){}
function gAc(){}
function vAc(){}
function IAc(){}
function OAc(){}
function JAc(a){this.a=a}
function Bvb(a){this.a=a}
function Fvb(a){this.a=a}
function QAc(a){this.c=a;this.b=a.a.b.a}
function FAc(a){GAc.call(this,a,null,null)}
function hAc(a,b){return a.c.Ud(b)}
function kAc(a,b){if(a.a){EAc(b);DAc(b)}}
function EAc(a){a.a.b=a.b;a.b.a=a.a;a.a=a.b=null}
function DAc(a){var b;b=a.c.b.b;a.b=b;a.a=a.c.b;b.a=a.c.b.b=a}
function PAc(a){if(a.b==a.c.a.b){throw new ZAc}a.a=a.b;a.b=a.b.a;return a.a}
function iAc(a,b){var c;c=CP(a.c.Xd(b),147);if(c){kAc(a,c);return c.e}return null}
function GAc(a,b,c){this.c=a;yAc.call(this,b,c);this.a=this.b=null}
function lAc(){Quc(this);this.b=new FAc(this);this.c=new Ozc;this.b.b=this.b;this.b.a=this.b}
function nbb(a){var b,c;b=CP(a.a.Xd(ked),139);if(b==null){c=sP(v5,{125:1,136:1,139:1},1,[led,f2c,med]);a.a.Zd(ked,c);return c}else{return b}}
function jAc(a,b,c){var d,e,f;e=CP(a.c.Xd(b),147);if(!e){d=new GAc(a,b,c);a.c.Zd(b,d);DAc(d);return null}else{f=e.e;xAc(e,c);kAc(a,e);return f}}
function lvb(b){var a,c,d,e,f;e=J6b(b.d,b.d.Q.selectedIndex);c=CP(iAc(b.f,e),113);try{f=Wqc(tl(b.e.Q,S0c));d=Wqc(tl(b.c.Q,S0c));OWb(b.a,c,d,f)}catch(a){a=E5(a);if(EP(a,135)){return}else throw a}}
function jvb(a){var b,c,d,e;d=new I3b;a.e=new M9b;re(a.e,ned);A9b(a.e,oed);a.c=new M9b;re(a.c,ned);A9b(a.c,ped);a.d=new P6b;y3b(d,0,0,qed);B3b(d,0,1,a.d);y3b(d,1,0,red);B3b(d,1,1,a.e);y3b(d,2,0,sed);B3b(d,2,1,a.c);for(c=Dwc(xC(a.f));c.a.ge();){b=CP(Kwc(c),1);K6b(a.d,b)}Ie(a.d,new Bvb(a),(kq(),kq(),jq));e=new Fvb(a);Ie(a.e,e,(rr(),rr(),qr));Ie(a.c,e,qr);return d}
function kvb(a){var b,c,d,e,f,g,i,j;a.f=new lAc;a.a=new QWb;me(a.a,Rdd,Rdd);he(a.a,Sdd);j=nbb(a.b);i=new M0b(ted);JWb(a.a,i,10,20);jAc(a.f,j[0],i);c=new TXb(ued);JWb(a.a,c,80,45);jAc(a.f,j[1],c);d=new l4b(2,3);d.o[iKc]=dQc;for(e=0;e<3;++e){y3b(d,0,e,e+fDc);B3b(d,1,e,new wSb((Vab(),scb(),jcb(),ecb)))}JWb(a.a,d,60,100);jAc(a.f,j[2],d);b=new S_b;kf(b,a.a);g=new S_b;kf(g,jvb(a));f=new M5b;f.e[EQc]=10;J5b(f,g);J5b(f,b);return f}
var oed='100',ned='3em',ped='60',red='<b>Bord du dessus:<\/b>',sed='<b>Bord gauche:<\/b>',qed='<b>Points \xE0 circuler:<\/b>',led='Bonjour le monde',ued='Cliquez-moi!',wed='CwAbsolutePanel$3',xed='CwAbsolutePanel$4',ted='Hello World',yed='LinkedHashMap',zed='LinkedHashMap$ChainEntry',Aed='LinkedHashMap$EntrySet',Bed='LinkedHashMap$EntrySet$EntryIterator',ved='No current entry',ked='cwAbsolutePanelWidgetNames';_=vvb.prototype;_.bc=function zvb(){X9(this.b,kvb(this.a))};_=Bvb.prototype=Avb.prototype=new Y;_.gC=function Cvb(){return zX};_.pc=function Dvb(a){mvb(this.a)};_.cM={21:1,44:1};_.a=null;_=Fvb.prototype=Evb.prototype=new Y;_.gC=function Gvb(){return AX};_.sc=function Hvb(a){lvb(this.a)};_.cM={27:1,44:1};_.a=null;_=lAc.prototype=gAc.prototype=new Nzc;_.oh=function mAc(){this.c.oh();this.b.b=this.b;this.b.a=this.b};_.Ud=function nAc(a){return this.c.Ud(a)};_.Vd=function oAc(a){var b;b=this.b.a;while(b!=this.b){if(KCc(b.e,a)){return true}b=b.a}return false};_.Wd=function pAc(){return new JAc(this)};_.Xd=function qAc(a){return iAc(this,a)};_.gC=function rAc(){return D4};_.Zd=function sAc(a,b){return jAc(this,a,b)};_.$d=function tAc(a){var b;b=CP(this.c.$d(a),147);if(b){EAc(b);return b.e}return null};_._d=function uAc(){return this.c._d()};_.cM={125:1,149:1};_.a=false;_=GAc.prototype=FAc.prototype=vAc.prototype=new wAc;_.gC=function HAc(){return A4};_.cM={147:1,150:1};_.a=null;_.b=null;_.c=null;_=JAc.prototype=IAc.prototype=new TC;_.ce=function KAc(a){var b,c,d;if(!EP(a,150)){return false}b=CP(a,150);c=b.je();if(hAc(this.a,c)){d=iAc(this.a,c);return KCc(b.yc(),d)}return false};_.gC=function LAc(){return C4};_.Pb=function MAc(){return new QAc(this)};_._d=function NAc(){return this.a.c._d()};_.cM={144:1,153:1};_.a=null;_=QAc.prototype=OAc.prototype=new Y;_.gC=function RAc(){return B4};_.ge=function SAc(){return this.b!=this.c.a.b};_.he=function TAc(){return PAc(this)};_.ie=function UAc(){if(!this.a){throw new drc(ved)}EAc(this.a);this.c.a.c.$d(this.a.d);this.a=null};_.a=null;_.b=null;_.c=null;var zX=Kqc(gXc,wed),AX=Kqc(gXc,xed),D4=Kqc(VUc,yed),A4=Kqc(VUc,zed),C4=Kqc(VUc,Aed),B4=Kqc(VUc,Bed);bDc(sj)(26);