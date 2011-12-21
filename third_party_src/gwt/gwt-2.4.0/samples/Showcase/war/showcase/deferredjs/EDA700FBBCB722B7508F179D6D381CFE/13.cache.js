function fA(){}
function hA(){hA=d2c;gA=new g_c}
function iA(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.$d(c)}}
function Jvc(a,b,c,d){var e;a.a.Eg(b,c);e=Lvc(a.a.i,b,c);Ce(e,d,true)}
function nA(){hA();var a;a=tgb(gA.Vd(E9c),51);if(!a){a=new lA;gA.Xd(E9c,a)}return a}
function jA(c,b){try{typeof $wnd[b]!='object'&&pA(b);c.a=$wnd[b]}catch(a){pA(b)}}
function pA(a){throw new o0c($3c+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function kA(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Cd(a);return String(c)}
function lA(){this.b='Dictionary userInfo';jA(this,E9c);if(!this.a){throw new o0c("Cannot find JavaScript object with the name 'userInfo'")}}
function NSb(){var a,b,c,d,e,f,g,i,j,k,n;f=new eKc;g=new Hsc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.Q.dir=s3c;g.Q.style['textAlign']=N3c;bKc(f,new Hsc('<b>This example interacts with the following JavaScript variable:<\/b>'));bKc(f,g);j=new Avc;b=j.j;i=nA();e=(n=new p_c,iA(i,n),n);a=0;for(d=XXc(bI(e.a));d.a.ee();){c=tgb(cYc(d),1);k=kA(i,c);qvc(j,0,a,c);Jvc(b,0,a,'cw-DictionaryExample-header');qvc(j,1,a,k);Jvc(b,1,a,'cw-DictionaryExample-data');++a}bKc(f,new Hsc('<br><br>'));bKc(f,j);return f}
var E9c='userInfo';_=lA.prototype=fA.prototype=new Y;_.gC=function mA(){return Mjb};_.Cd=function oA(a){var b;b="Cannot find '"+a+"' in "+this;throw new o0c(b)};_.tS=function qA(){return this.b};_.cM={51:1};_.a=null;_.b=null;var gA;_=RSb.prototype;_.ac=function VSb(){jDb(this.a,NSb())};var Mjb=cSc(B7c,h5c);v2c(tj)(13);