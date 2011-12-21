function _A(){}
function WA(){}
function olb(){}
function slb(){}
function _Xb(){}
function gYb(a,b){Ev(a.a,b)}
function plb(a,b){this.b=a;this.a=b}
function tlb(a,b){this.b=a;this.a=b}
function BB(a){return AW(mB,a)}
function $A(){$A=spc;ZA=new _A}
function flb(a,b){jPb(b,A3c+a.Lf()+vsc+a.Mf())}
function wYb(){pYb();uYb.call(this,Bl($doc,L3c),M3c)}
function P6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function O6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(N3c,-65535)}catch(a){return 0}}
function elb(a,b){var c,d;c=new tUb;c.e[lDc]=4;qUb(c,a);if(b){d=new mPb(z3c);Ie(a,new plb(a,d),(rr(),rr(),qr));Ie(a,new tlb(a,d),(xq(),xq(),wq));qUb(c,d)}return c}
function R6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(N3c,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints(P3c,f)<=0){e+=2;f.moveEnd(N3c,-1);g=f.text.length}return d+e}catch(a){return 0}}
function Q6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint(O3c,c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(N3c,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(N3c,-1);i=g.text.length}return e+f}catch(a){return 0}}
var F3c='<b>\u5E38\u89C4\u6587\u672C\u6846:<\/b>',I3c='<br><br><b>\u5BC6\u7801\u6587\u672C\u6846:<\/b>',K3c='<br><br><b>\u6587\u672C\u533A\u57DF:<\/b>',Q3c='AnyRtlDirectionEstimator',R3c='CwBasicText$2',S3c='CwBasicText$3',O3c='EndToStart',T3c='PasswordTextBox',P3c='StartToEnd',N3c='character',G3c='cwBasicText-password',H3c='cwBasicText-password-disabled',J3c='cwBasicText-textarea',B3c='cwBasicText-textbox',C3c='cwBasicText-textbox-disabled',M3c='gwt-PasswordTextBox',L3c='password',D3c='\u53EA\u8BFB',A3c='\u5DF2\u9009\u62E9: ',z3c='\u5DF2\u9009\u62E9: 0, 0';_=_A.prototype=WA.prototype=new XA;_.hd=function aB(a){return BB((vB(),a))?(my(),ly):(my(),ky)};_.gC=function bB(){return VF};var ZA;_=jlb.prototype;_.bc=function nlb(){var a,b,c,d,e,f;FY(this.a,(f=new e5b,f.e[lDc]=5,d=new tYb,C4b(d.Q,Opc,B3c),gYb(d,($A(),$A(),ZA)),b=new tYb,C4b(b.Q,Opc,C3c),b.Q[zPc]=D3c,Dv(b.a),b.Q[E3c]=true,b5b(f,new tPb(F3c)),b5b(f,elb(d,true)),b5b(f,elb(b,false)),c=new wYb,C4b(c.Q,Opc,G3c),a=new wYb,C4b(a.Q,Opc,H3c),a.Q[zPc]=D3c,Dv(a.a),a.Q[E3c]=true,b5b(f,new tPb(I3c)),b5b(f,elb(c,true)),b5b(f,elb(a,false)),e=new E2b,C4b(e.Q,Opc,J3c),e.Q.rows=5,b5b(f,new tPb(K3c)),b5b(f,elb(e,true)),f))};_=plb.prototype=olb.prototype=new Y;_.gC=function qlb(){return QK};_.sc=function rlb(a){flb(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=tlb.prototype=slb.prototype=new Y;_.gC=function ulb(){return RK};_.qc=function vlb(a){flb(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=cYb.prototype;_.Lf=function kYb(){return O6b(this.Q)};_.Mf=function lYb(){return P6b(this.Q)};_=wYb.prototype=_Xb.prototype=new aYb;_.gC=function xYb(){return xP};_.cM={40:1,46:1,85:1,92:1,96:1,111:1,113:1};_=D2b.prototype;_.Lf=function G2b(){return Q6b(this.Q)};_.Mf=function H2b(){return R6b(this.Q)};var VF=rdc(KHc,Q3c),QK=rdc(rKc,R3c),RK=rdc(rKc,S3c),xP=rdc(jFc,T3c);Kpc(sj)(39);