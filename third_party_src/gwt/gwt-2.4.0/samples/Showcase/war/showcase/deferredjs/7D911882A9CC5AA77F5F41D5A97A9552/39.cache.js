function PN(){}
function KN(){}
function vxb(){}
function zxb(){}
function i7b(){}
function p7b(a,b){vv(a.a,b)}
function pO(a){return $6(aO,a)}
function ON(){ON=gAc;NN=new PN}
function wxb(a,b){this.b=a;this.a=b}
function Axb(a,b){this.b=a;this.a=b}
function mxb(a,b){y$b(b,'S\xE9lectionn\xE9: '+a.Gg()+zBc+a.Hg())}
function F7b(){y7b();D7b.call(this,Bl($doc,'password'),'gwt-PasswordTextBox')}
function Dhc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function Chc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(QJc,-65535)}catch(a){return 0}}
function lxb(a,b){var c,d;c=new F3b;c.e[mEc]=4;C3b(c,a);if(b){d=new B$b('S\xE9lectionn\xE9: 0, 0');Ie(a,new wxb(a,d),(ir(),ir(),hr));Ie(a,new Axb(a,d),(oq(),oq(),nq));C3b(c,d)}return c}
function Fhc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(QJc,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints('StartToEnd',f)<=0){e+=2;f.moveEnd(QJc,-1);g=f.text.length}return d+e}catch(a){return 0}}
function Ehc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint('EndToStart',c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(QJc,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(QJc,-1);i=g.text.length}return e+f}catch(a){return 0}}
var QJc='character',OJc='lecture seulement';_=PN.prototype=KN.prototype=new LN;_.je=function QN(a){return pO((jO(),a))?(rz(),qz):(rz(),pz)};_.gC=function RN(){return cT};var NN;_=qxb.prototype;_.ac=function uxb(){var a,b,c,d,e,f;d9(this.a,(f=new fgc,f.e[mEc]=5,d=new C7b,Dfc(d.Q,AAc,'cwBasicText-textbox'),p7b(d,(ON(),ON(),NN)),b=new C7b,Dfc(b.Q,AAc,'cwBasicText-textbox-disabled'),b.Q[VFc]=OJc,uv(b.a),b.Q[PJc]=true,cgc(f,new I$b('<b>Zone de texte normale:<\/b>')),cgc(f,lxb(d,true)),cgc(f,lxb(b,false)),c=new F7b,Dfc(c.Q,AAc,'cwBasicText-password'),a=new F7b,Dfc(a.Q,AAc,'cwBasicText-password-disabled'),a.Q[VFc]=OJc,uv(a.a),a.Q[PJc]=true,cgc(f,new I$b('<br><br><b>Zone de texte &laquo;mot de passe&raquo;:<\/b>')),cgc(f,lxb(c,true)),cgc(f,lxb(a,false)),e=new Hdc,Dfc(e.Q,AAc,'cwBasicText-textarea'),e.Q.rows=5,cgc(f,new I$b('<br><br><b>Zone de texte:<\/b>')),cgc(f,lxb(e,true)),f))};_=wxb.prototype=vxb.prototype=new Y;_.gC=function xxb(){return WX};_.qc=function yxb(a){mxb(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=Axb.prototype=zxb.prototype=new Y;_.gC=function Bxb(){return XX};_.oc=function Cxb(a){mxb(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=l7b.prototype;_.Gg=function t7b(){return Chc(this.Q)};_.Hg=function u7b(){return Dhc(this.Q)};_=F7b.prototype=i7b.prototype=new j7b;_.gC=function G7b(){return C0};_.cM={40:1,46:1,84:1,91:1,95:1,110:1,112:1};_=Gdc.prototype;_.Gg=function Jdc(){return Ehc(this.Q)};_.Hg=function Kdc(){return Fhc(this.Q)};var cT=foc(kFc,'AnyRtlDirectionEstimator'),WX=foc(xFc,'CwBasicText$2'),XX=foc(xFc,'CwBasicText$3'),C0=foc(WEc,'PasswordTextBox');yAc(sj)(39);