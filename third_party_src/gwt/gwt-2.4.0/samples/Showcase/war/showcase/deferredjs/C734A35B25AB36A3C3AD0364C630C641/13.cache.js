function lA(){}
function nA(){nA=FRc;mA=new IOc}
function Zic(a,b,c,d){var e;a.b.Mg(b,c);e=_ic(a.b.j,b,c);Qe(e,d,true)}
function oA(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ge(c)}}
function qA(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Kd(a);return String(c)}
function tA(){nA();var a;a=d4(mA.be(TYc),51);if(!a){a=new rA;mA.de(TYc,a)}return a}
function pA(c,b){try{typeof $wnd[b]!='object'&&vA(b);c.b=$wnd[b]}catch(a){vA(b)}}
function vA(a){throw new QPc(HTc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function rA(){this.c='Dictionary userInfo';pA(this,TYc);if(!this.b){throw new QPc("Cannot find JavaScript object with the name 'userInfo'")}}
function eGb(){var a,b,c,d,e,f,g,i,j,k,n;f=new vxc;g=new Xfc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=cTc;g.R.style['textAlign']=uTc;sxc(f,new Xfc('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));sxc(f,g);j=new Qic;b=j.k;i=tA();e=(n=new ROc,oA(i,n),n);a=0;for(d=xLc(hH(e.b));d.b.me();){c=d4(ELc(d),1);k=qA(i,c);Gic(j,0,a,c);Zic(b,0,a,'cw-DictionaryExample-header');Gic(j,1,a,k);Zic(b,1,a,'cw-DictionaryExample-data');++a}sxc(f,new Xfc('<br><br>'));sxc(f,j);return f}
var TYc='userInfo';_=rA.prototype=lA.prototype=new Y;_.gC=function sA(){return A7};_.Kd=function uA(a){var b;b="Cannot find '"+a+"' in "+this;throw new QPc(b)};_.tS=function wA(){return this.c};_.cM={51:1};_.b=null;_.c=null;var mA;_=iGb.prototype;_.fc=function mGb(){Cqb(this.b,eGb())};var A7=EFc(WWc,'Dictionary');XRc(Hj)(13);