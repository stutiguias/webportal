using System.Web;
using System.Web.Optimization;

namespace WebPortal
{
    public class BundleConfig
    {
        public static void RegisterBundles(BundleCollection bundles)
        {

            bundles.Add(new ScriptBundle("~/bundles/jqueryval").Include(
                        "~/js/jquery.unobtrusive*",
                        "~/js/jquery.validate*"));

            bundles.Add(new ScriptBundle("~/bundles/modernizr").Include(
                        "~/js/modernizr-*"));

            bundles.Add(new StyleBundle("~/css").Include("~/css/main.css"));

            bundles.Add(new StyleBundle("~/css/dark-hive/").Include("~/css/dark-hive/jquery-ui-1.8.18.custom.css"));
        }
    }
}