function SA(){}
function NA(){}
function blb(){}
function flb(){}
function OXb(){}
function VXb(a,b){Ev(a.a,b)}
function clb(a,b){this.b=a;this.a=b}
function glb(a,b){this.b=a;this.a=b}
function sB(a){return mW(dB,a)}
function RA(){RA=fpc;QA=new SA}
function Ukb(a,b){YOb(b,h3c+a.Rf()+isc+a.Sf())}
function jYb(){cYb();hYb.call(this,Bl($doc,s3c),t3c)}
function C6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function B6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(u3c,-65535)}catch(a){return 0}}
function Tkb(a,b){var c,d;c=new gUb;c.e[YCc]=4;dUb(c,a);if(b){d=new _Ob(g3c);Ie(a,new clb(a,d),(rr(),rr(),qr));Ie(a,new glb(a,d),(xq(),xq(),wq));dUb(c,d)}return c}
function E6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(u3c,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints(w3c,f)<=0){e+=2;f.moveEnd(u3c,-1);g=f.text.length}return d+e}catch(a){return 0}}
function D6b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint(v3c,c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(u3c,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(u3c,-1);i=g.text.length}return e+f}catch(a){return 0}}
var m3c='<b>Normal text box:<\/b>',p3c='<br><br><b>Password text box:<\/b>',r3c='<br><br><b>Text area:<\/b>',x3c='AnyRtlDirectionEstimator',y3c='CwBasicText$2',z3c='CwBasicText$3',v3c='EndToStart',A3c='PasswordTextBox',h3c='Selected: ',g3c='Selected: 0, 0',w3c='StartToEnd',u3c='character',n3c='cwBasicText-password',o3c='cwBasicText-password-disabled',q3c='cwBasicText-textarea',i3c='cwBasicText-textbox',j3c='cwBasicText-textbox-disabled',t3c='gwt-PasswordTextBox',s3c='password',k3c='read only';_=SA.prototype=NA.prototype=new OA;_.od=function TA(a){return sB((mB(),a))?(_x(),$x):(_x(),Zx)};_.gC=function UA(){return HF};var QA;_=Ykb.prototype;_.bc=function alb(){var a,b,c,d,e,f;rY(this.a,(f=new T4b,f.e[YCc]=5,d=new gYb,p4b(d.Q,Bpc,i3c),VXb(d,(RA(),RA(),QA)),b=new gYb,p4b(b.Q,Bpc,j3c),b.Q[kPc]=k3c,Dv(b.a),b.Q[l3c]=true,Q4b(f,new gPb(m3c)),Q4b(f,Tkb(d,true)),Q4b(f,Tkb(b,false)),c=new jYb,p4b(c.Q,Bpc,n3c),a=new jYb,p4b(a.Q,Bpc,o3c),a.Q[kPc]=k3c,Dv(a.a),a.Q[l3c]=true,Q4b(f,new gPb(p3c)),Q4b(f,Tkb(c,true)),Q4b(f,Tkb(a,false)),e=new r2b,p4b(e.Q,Bpc,q3c),e.Q.rows=5,Q4b(f,new gPb(r3c)),Q4b(f,Tkb(e,true)),f))};_=clb.prototype=blb.prototype=new Y;_.gC=function dlb(){return CK};_.sc=function elb(a){Ukb(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=glb.prototype=flb.prototype=new Y;_.gC=function hlb(){return DK};_.qc=function ilb(a){Ukb(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=RXb.prototype;_.Rf=function ZXb(){return B6b(this.Q)};_.Sf=function $Xb(){return C6b(this.Q)};_=jYb.prototype=OXb.prototype=new PXb;_.gC=function kYb(){return jP};_.cM={40:1,46:1,84:1,91:1,95:1,110:1,112:1};_=q2b.prototype;_.Rf=function t2b(){return D6b(this.Q)};_.Sf=function u2b(){return E6b(this.Q)};var HF=edc(vHc,x3c),CK=edc(cKc,y3c),DK=edc(cKc,z3c),jP=edc(WEc,A3c);xpc(sj)(39);