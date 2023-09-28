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
            path: '/docs/about/02_getting_started',
            meta: {
                h1: 'Getting Started',
                title: 'Getting Started',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-about-02_getting_started', '/docs/about/02_getting_started', {})
        },
        {
            path: '/docs/about/03_authentication',
            meta: {
                h1: 'Custom Authentication',
                title: 'Custom Authentication',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-about-03_authentication', '/docs/about/03_authentication', {})
        },
        {
            path: '/docs/secrets/01_introduction',
            meta: {
                h1: 'Introduction',
                title: 'Introduction',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-secrets-01_introduction', '/docs/secrets/01_introduction', {})
        },
        {
            path: '/docs/secrets/02_ssl',
            meta: {
                h1: 'Store SSL Keys & Certs in Secrets',
                title: 'Store SSL Keys & Certs in Secrets',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-secrets-02_ssl', '/docs/secrets/02_ssl', {})
        },
        {
            path: '/docs/secrets/03_password_provider',
            meta: {
                h1: 'Store Passwords in Secrets',
                title: 'Store Passwords in Secrets',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-secrets-03_password_provider', '/docs/secrets/03_password_provider', {})
        },
        {
            path: '/docs/secrets/04_secrets_client',
            meta: {
                h1: 'Custom Secrets Client',
                title: 'Custom Secrets Client',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-secrets-04_secrets_client', '/docs/secrets/04_secrets_client', {})
        },
        {
            path: '/docs/secrets/05_vault_client',
            meta: {
                h1: 'Custom Vault Client',
                title: 'Custom Vault Client',
                h1Prefix: null,
                description: null,
                keywords: null,
                customLayout: null,
                hasNav: true
            },
            component: loadPage('docs-secrets-05_vault_client', '/docs/secrets/05_vault_client', {})
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
                                { href: '/docs/about/02_getting_started', title: 'Getting Started' },
                                { href: '/docs/about/03_authentication', title: 'Custom Authentication' }
                            ]
                        },
                        {
                            title: 'OCI Secrets Service',
                            action: 'visibility_off',
                            group: '/docs/secrets',
                            items: [
                                { href: '/docs/secrets/01_introduction', title: 'Introduction' },
                                { href: '/docs/secrets/02_ssl', title: 'Store SSL Keys & Certs in Secrets' },
                                { href: '/docs/secrets/03_password_provider', title: 'Store Passwords in Secrets' },
                                { href: '/docs/secrets/04_secrets_client', title: 'Custom Secrets Client' },
                                { href: '/docs/secrets/05_vault_client', title: 'Custom Vault Client' }
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