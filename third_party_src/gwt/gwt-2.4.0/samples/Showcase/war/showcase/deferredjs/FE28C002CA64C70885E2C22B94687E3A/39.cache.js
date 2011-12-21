function hfb(){}
function cfb(){}
function Y0b(){}
function a1b(){}
function JDc(){}
function QDc(a,b){Ev(a.a,b)}
function Z0b(a,b){this.b=a;this.a=b}
function b1b(a,b){this.b=a;this.a=b}
function Jfb(a){return hCb(ufb,a)}
function gfb(){gfb=a5c;ffb=new hfb}
function P0b(a,b){Tuc(b,gLd+a.Og()+d8c+a.Pg())}
function eEc(){ZDc();cEc.call(this,Bl($doc,rLd),sLd)}
function xOc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function wOc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(tLd,-65535)}catch(a){return 0}}
function O0b(a,b){var c,d;c=new bAc;c.e[Uid]=4;$zc(c,a);if(b){d=new Wuc(fLd);Ie(a,new Z0b(a,d),(rr(),rr(),qr));Ie(a,new b1b(a,d),(xq(),xq(),wq));$zc(c,d)}return c}
function zOc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(tLd,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints(vLd,f)<=0){e+=2;f.moveEnd(tLd,-1);g=f.text.length}return d+e}catch(a){return 0}}
function yOc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint(uLd,c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(tLd,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(tLd,-1);i=g.text.length}return e+f}catch(a){return 0}}
var lLd='<b>Normal text box:<\/b>',oLd='<br><br><b>Password text box:<\/b>',qLd='<br><br><b>Text area:<\/b>',wLd='AnyRtlDirectionEstimator',xLd='CwBasicText$2',yLd='CwBasicText$3',uLd='EndToStart',zLd='PasswordTextBox',gLd='Selected: ',fLd='Selected: 0, 0',vLd='StartToEnd',tLd='character',mLd='cwBasicText-password',nLd='cwBasicText-password-disabled',pLd='cwBasicText-textarea',hLd='cwBasicText-textbox',iLd='cwBasicText-textbox-disabled',sLd='gwt-PasswordTextBox',rLd='password',jLd='read only';_=hfb.prototype=cfb.prototype=new dfb;_.le=function ifb(a){return Jfb((Dfb(),a))?(QA(),PA):(QA(),OA)};_.gC=function jfb(){return Clb};var ffb;_=T0b.prototype;_.bc=function X0b(){var a,b,c,d,e,f;mEb(this.a,(f=new OMc,f.e[Uid]=5,d=new bEc,kMc(d.Q,w5c,hLd),QDc(d,(gfb(),gfb(),ffb)),b=new bEc,kMc(b.Q,w5c,iLd),b.Q[gvd]=jLd,Dv(b.a),b.Q[kLd]=true,LMc(f,new bvc(lLd)),LMc(f,O0b(d,true)),LMc(f,O0b(b,false)),c=new eEc,kMc(c.Q,w5c,mLd),a=new eEc,kMc(a.Q,w5c,nLd),a.Q[gvd]=jLd,Dv(a.a),a.Q[kLd]=true,LMc(f,new bvc(oLd)),LMc(f,O0b(c,true)),LMc(f,O0b(a,false)),e=new mKc,kMc(e.Q,w5c,pLd),e.Q.rows=5,LMc(f,new bvc(qLd)),LMc(f,O0b(e,true)),f))};_=Z0b.prototype=Y0b.prototype=new Y;_.gC=function $0b(){return xqb};_.sc=function _0b(a){P0b(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=b1b.prototype=a1b.prototype=new Y;_.gC=function c1b(){return yqb};_.qc=function d1b(a){P0b(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=MDc.prototype;_.Og=function UDc(){return wOc(this.Q)};_.Pg=function VDc(){return xOc(this.Q)};_=eEc.prototype=JDc.prototype=new KDc;_.gC=function fEc(){return evb};_.cM={40:1,46:1,85:1,92:1,96:1,111:1,113:1};_=lKc.prototype;_.Og=function oKc(){return yOc(this.Q)};_.Pg=function pKc(){return zOc(this.Q)};var Clb=_Uc(rnd,wLd),xqb=_Uc($pd,xLd),yqb=_Uc($pd,yLd),evb=_Uc(Skd,zLd);s5c(sj)(39);