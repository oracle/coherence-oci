function createConfig() {
    return {
        home: "docs/about/01_overview",
        release: "1.0.0-SNAPSHOT",
        releases: [
            "1.0.0-SNAPSHOT"
        ],
        pathColors: {
            "*": "blue-grey"
        },
        theme: {
            primary: '#1976D2',
            secondary: '#424242',
            accent: '#82B1FF',
            error: '#FF5252',
            info: '#2196F3',
            success: '#4CAF50',
            warning: '#FFC107'
        },
        navTitle: 'Oracle Coherence & OCI',
        navIcon: null,
        navLogo: 'docs/images/logo.png'
    };
}

function createRoutes(){
    return [
        {
            path: '/docs/about/01_overview',
            meta: {
                h1: 'Overview',
                title: 'Overview',
                h1Prefix: null,
                description: 'Oracle Coherence OCI Documentation',
                keywords: 'coherence, OCI, java, documentation',
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-about-01_overview', '/docs/about/01_overview', {})
        },
        {
            path: '/docs/about/02_introduction',
            meta: {
                h1: 'Introduction',
                title: 'Introduction',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-about-02_introduction', '/docs/about/02_introduction', {})
        },
        {
            path: '/docs/secrets/02_introduction',
            meta: {
                h1: 'Introduction',
                title: 'Introduction',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-secrets-02_introduction', '/docs/secrets/02_introduction', {})
        },
        {
            path: '/README',
            meta: {
                h1: 'Coherence Integrations with Oracle Cloud Infrastructure (OCI)',
                title: 'Coherence Integrations with Oracle Cloud Infrastructure (OCI)',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: false
            },
            component: loadPage('README', '/README', {})
        },
        {
            path: '/coherence-oci-secrets/README',
            meta: {
                h1: 'OCI Secrets Service Integration',
                title: 'OCI Secrets Service Integration',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: false
            },
            component: loadPage('coherence-oci-secrets-README', '/coherence-oci-secrets/README', {})
        },
        {
            path: '/', redirect: '/docs/about/01_overview'
        },
        {
            path: '*', redirect: '/'
        }
    ];
}

function createNav(){
    return [
        {
            groups: [
                {
                    title: 'Documentation',
                    group: '/docs',
                    items: [
                        {
                            title: 'About',
                            action: 'assistant',
                            group: '/docs/about',
                            items: [
                                { href: '/docs/about/01_overview', title: 'Overview' },
                                { href: '/docs/about/02_introduction', title: 'Introduction' }
                            ]
                        },
                        {
                            title: 'OCI Secrets Service',
                            action: 'visibility_off',
                            group: '/docs/secrets',
                            items: [
                                { href: '/docs/secrets/02_introduction', title: 'Introduction' }
                            ]
                        }
                    ]
                },
            ]
        }
        ,{ header: 'Additional Resources' },
        {
            title: 'Slack',
            action: 'fa-slack',
            href: 'https://join.slack.com/t/oraclecoherence/shared_invite/enQtNzcxNTQwMTAzNjE4LTJkZWI5ZDkzNGEzOTllZDgwZDU3NGM2YjY5YWYwMzM3ODdkNTU2NmNmNDFhOWIxMDZlNjg2MzE3NmMxZWMxMWE',
            target: '_blank'
        },
        {
            title: 'Coherence Web Site',
            action: 'fa-globe',
            href: 'https://coherence.community/',
            target: '_blank'
        },
        {
            title: 'GitHub',
            action: 'fa-github-square',
            href: 'https://github.com/oracle/coherence-oci/',
            target: '_blank'
        },
        {
            title: 'Twitter',
            action: 'fa-twitter-square',
            href: 'https://twitter.com/OracleCoherence/',
            target: '_blank'
        }
    ];
}