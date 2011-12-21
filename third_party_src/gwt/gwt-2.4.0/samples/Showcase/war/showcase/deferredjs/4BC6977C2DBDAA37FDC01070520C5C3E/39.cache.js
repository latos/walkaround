function JA(){}
function EA(){}
function EVb(){}
function Rjb(){}
function Vjb(){}
function LVb(a,b){vv(a.a,b)}
function Sjb(a,b){this.b=a;this.a=b}
function Wjb(a,b){this.b=a;this.a=b}
function jB(a){return uV(WA,a)}
function IA(){IA=Cmc;HA=new JA}
function Ijb(a,b){UMb(b,'Selected: '+a.Jf()+Vnc+a.Kf())}
function _Vb(){UVb();ZVb.call(this,Bl($doc,'password'),'gwt-PasswordTextBox')}
function Z3b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function Y3b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(bwc,-65535)}catch(a){return 0}}
function Hjb(a,b){var c,d;c=new _Rb;c.e[Fqc]=4;YRb(c,a);if(b){d=new XMb('Selected: 0, 0');Ie(a,new Sjb(a,d),(ir(),ir(),hr));Ie(a,new Wjb(a,d),(oq(),oq(),nq));YRb(c,d)}return c}
function _3b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(bwc,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints('StartToEnd',f)<=0){e+=2;f.moveEnd(bwc,-1);g=f.text.length}return d+e}catch(a){return 0}}
function $3b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint('EndToStart',c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(bwc,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(bwc,-1);i=g.text.length}return e+f}catch(a){return 0}}
var bwc='character',_vc='read only';_=JA.prototype=EA.prototype=new FA;_.md=function KA(a){return jB((dB(),a))?(Sx(),Rx):(Sx(),Qx)};_.gC=function LA(){return yF};var HA;_=Mjb.prototype;_.ac=function Qjb(){var a,b,c,d,e,f;zX(this.a,(f=new B2b,f.e[Fqc]=5,d=new YVb,Z1b(d.Q,Wmc,'cwBasicText-textbox'),LVb(d,(IA(),IA(),HA)),b=new YVb,Z1b(b.Q,Wmc,'cwBasicText-textbox-disabled'),b.Q[msc]=_vc,uv(b.a),b.Q[awc]=true,y2b(f,new cNb('<b>Normal text box:<\/b>')),y2b(f,Hjb(d,true)),y2b(f,Hjb(b,false)),c=new _Vb,Z1b(c.Q,Wmc,'cwBasicText-password'),a=new _Vb,Z1b(a.Q,Wmc,'cwBasicText-password-disabled'),a.Q[msc]=_vc,uv(a.a),a.Q[awc]=true,y2b(f,new cNb('<br><br><b>Password text box:<\/b>')),y2b(f,Hjb(c,true)),y2b(f,Hjb(a,false)),e=new b0b,Z1b(e.Q,Wmc,'cwBasicText-textarea'),e.Q.rows=5,y2b(f,new cNb('<br><br><b>Text area:<\/b>')),y2b(f,Hjb(e,true)),f))};_=Sjb.prototype=Rjb.prototype=new Y;_.gC=function Tjb(){return qK};_.qc=function Ujb(a){Ijb(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=Wjb.prototype=Vjb.prototype=new Y;_.gC=function Xjb(){return rK};_.oc=function Yjb(a){Ijb(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=HVb.prototype;_.Jf=function PVb(){return Y3b(this.Q)};_.Kf=function QVb(){return Z3b(this.Q)};_=_Vb.prototype=EVb.prototype=new FVb;_.gC=function aWb(){return YO};_.cM={40:1,46:1,83:1,90:1,94:1,109:1,111:1};_=a0b.prototype;_.Jf=function d0b(){return $3b(this.Q)};_.Kf=function e0b(){return _3b(this.Q)};var yF=Bac(Drc,'AnyRtlDirectionEstimator'),qK=Bac(Qrc,'CwBasicText$2'),rK=Bac(Qrc,'CwBasicText$3'),YO=Bac(nrc,'PasswordTextBox');Umc(sj)(39);